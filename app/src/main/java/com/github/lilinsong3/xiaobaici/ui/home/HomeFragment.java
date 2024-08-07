package com.github.lilinsong3.xiaobaici.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import dagger.hilt.android.AndroidEntryPoint;

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
        final Integer newOrientation = binding.homePager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL ?
                ViewPager2.ORIENTATION_VERTICAL : ViewPager2.ORIENTATION_HORIZONTAL;
        homeViewModel.switchBrowsingOrientation(newOrientation);
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
        homeViewModel.browsingOrientation.observe(viewLifecycleOwner, orientation -> {
            if (orientation != ViewPager2.ORIENTATION_VERTICAL && orientation != ViewPager2.ORIENTATION_HORIZONTAL) {
                return;
            }

            final int currItem = binding.homePager.getCurrentItem();
            // 改变 orientation 会 requestLayout()，currentItem 得不到恢复
            binding.homePager.setOrientation(orientation);
            // 恢复currentItem
            binding.homePager.post(() -> binding.homePager.setCurrentItem(currItem, false));

            final MenuItem switchOrientationMenuItem = binding.fHomeToolbar.getMenu().findItem(R.id.mi_switch_orientation);
            if (switchOrientationMenuItem != null) {
                switchOrientationMenuItem.setIcon(orientation == ViewPager2.ORIENTATION_VERTICAL ? R.drawable.ic_swipe_horiz : R.drawable.ic_swipe_vert);
            }
        });

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
                            if (binding != null && binding.homePager.getAdapter() == null) {
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
