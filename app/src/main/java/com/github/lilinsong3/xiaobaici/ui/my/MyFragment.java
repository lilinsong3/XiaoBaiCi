package com.github.lilinsong3.xiaobaici.ui.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.databinding.FragmentMyBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyFragment extends Fragment  {
    private static final String TAG = "MyFragment";
    private FragmentMyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupMenu();
        setupOnExitBtnClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // 导航菜单栏
    private void setupMenu() {
        binding.recyclerNavMenu.setAdapter(new MyNavMenuAdapter(generateItems()));
        final MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(), RecyclerView.VERTICAL);
        divider.setDividerInsetStart(getResources().getDimensionPixelOffset(R.dimen.limit_icon_size));
        divider.setLastItemDecorated(false);
        binding.recyclerNavMenu.addItemDecoration(divider);
    }

    private List<MyNavMenuAdapter.MyNavMenuItem> generateItems() {
        NavController navController = Navigation.findNavController(binding.getRoot());
        List<MyNavMenuAdapter.MyNavMenuItem> result = new ArrayList<>(3);
        result.add(
                new MyNavMenuAdapter.MyNavMenuItem(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_selected),
                        "收藏",
                        v -> navController.navigate(MyFragmentDirections.actionMyToFavorites())
                ));
        result.add(
                new MyNavMenuAdapter.MyNavMenuItem(ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_history),
                        "历史",
                        v -> navController.navigate(MyFragmentDirections.actionMyToHistories())
                ));
        result.add(
                new MyNavMenuAdapter.MyNavMenuItem(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_settings),
                        "设置",
                        v -> navController.navigate(MyFragmentDirections.actionMyToSettings())
                )
        );
        return result;
    }

    // 点击推出按钮
    private void setupOnExitBtnClick() {
        binding.btnExit.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.short_exit_app)
                .setMessage(R.string.long_confirm_exit_app)
                .setNegativeButton(R.string.short_cancel, (dialog, which) -> dialog.cancel())
                .setPositiveButton(R.string.short_sure, (dialog, which) -> {
                    dialog.dismiss();
                    requireActivity().finish();
                }).show()
        );
    }


}
