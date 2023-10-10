package com.github.lilinsong3.xiaobaici.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.lilinsong3.xiaobaici.MainActivityViewModel;
import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.databinding.FragmentHomeBinding;
import com.github.lilinsong3.xiaobaici.util.DataStoreUtil;
import com.github.lilinsong3.xiaobaici.util.RxUtil;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.github.lilinsong3.xiaobaici.ui.home.HomeViewModel.OFFSCREEN_PAGE_LIMIT;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private HanziWordFragmentStateAdapter hanziWordFragmentStateAdapter;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 切换浏览方向
    private boolean switchOrientation() {
        homeViewModel.setLoading(true);
        final Integer newOrientation = binding.homePager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL ?
                ViewPager2.ORIENTATION_VERTICAL : ViewPager2.ORIENTATION_HORIZONTAL;
        DataStoreUtil.ignoreSet(requireContext(), DataStoreUtil.HOME_VIEW_PAGER2_ORIENTATION, newOrientation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    homeViewModel.setLoading(false);
                    Toast.makeText(requireContext(), R.string.long_error, Toast.LENGTH_SHORT).show();
                })
                .to(RxUtil.autoDispose(getViewLifecycleOwner()))
                .subscribe();
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        NavController navController = Navigation.findNavController(view);
        binding.fHomeToolbar.inflateMenu(R.menu.toolbar_home_menu);
        NavigationUI.setupWithNavController(binding.fHomeToolbar, navController);
        binding.fHomeToolbar.setOnMenuItemClickListener(menuItem -> {
            Integer itemId = menuItem.getItemId();
            if (itemId.equals(R.id.mi_refresh)) {
                homeViewModel.refreshPageData();
                return true;
            }
            if (itemId.equals(R.id.mi_switch_orientation)) {
                return switchOrientation();
            }
            return NavigationUI.onNavDestinationSelected(menuItem, navController);
        });

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        // loading
//        binding.globalLoading.circularLoading.setVisibilityAfterHide(View.GONE);
        homeViewModel.showLoading.observe(viewLifecycleOwner, mainActivityViewModel::setGlobalLoading);

        // ViewPager2
        hanziWordFragmentStateAdapter = new HanziWordFragmentStateAdapter(this);
        hanziWordFragmentStateAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                homeViewModel.setViewPosition(position);
            }
        };

        // 设置滑动浏览方向
        DataStoreUtil.get(requireContext(), DataStoreUtil.HOME_VIEW_PAGER2_ORIENTATION)
                .subscribeOn(Schedulers.io())
                .onErrorReturnItem(ViewPager2.ORIENTATION_VERTICAL)
                .defaultIfEmpty(ViewPager2.ORIENTATION_VERTICAL)
                .filter(orientation -> orientation == ViewPager2.ORIENTATION_VERTICAL || orientation == ViewPager2.ORIENTATION_HORIZONTAL)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(orientation -> {
                    binding.homePager.setOrientation(orientation);
                    homeViewModel.setLoading(false);
                    final MenuItem switchOrientationMenuItem = binding.fHomeToolbar.getMenu().findItem(R.id.mi_switch_orientation);
                    if (switchOrientationMenuItem != null) {
                        // 垂直方向
                        if (orientation == ViewPager2.ORIENTATION_VERTICAL) {
                            switchOrientationMenuItem.setIcon(R.drawable.ic_swipe_vert);
                            // 限制生命周期是为了降低toast出现的频率
                            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                                Toast.makeText(requireContext(), R.string.long_vert_orientation_hint, Toast.LENGTH_SHORT).show();
                            }
                        }
                        // 水平方向
                        if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                            switchOrientationMenuItem.setIcon(R.drawable.ic_swipe_horiz);
                            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                                Toast.makeText(requireContext(), R.string.long_horiz_orientation_hint, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .doFinally(() -> homeViewModel.setLoading(false))
                .to(RxUtil.autoDispose(viewLifecycleOwner))
                .subscribe();

        // 离屏页面限制数量
        binding.homePager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
        // 监听页面切换，预加载数据
        binding.homePager.registerOnPageChangeCallback(pageChangeCallback);
        binding.homePager.setPageTransformer(new HanziWordPageTransformer());
        homeViewModel.getHanziWordIdData().observe(
                viewLifecycleOwner,
                data -> hanziWordFragmentStateAdapter.submit(
                        data,
                        () -> {
                            // must have data loaded first and then set adapter later so that ViewPager2 restores the position of page in case where configuration changes etc.
                            if (binding.homePager.getAdapter() == null) {
                                binding.homePager.setAdapter(hanziWordFragmentStateAdapter);
                            }
                        }
                )
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.homePager.unregisterOnPageChangeCallback(pageChangeCallback);
        binding = null;
    }

}
