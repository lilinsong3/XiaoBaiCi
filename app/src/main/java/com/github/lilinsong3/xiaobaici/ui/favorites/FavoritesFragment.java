package com.github.lilinsong3.xiaobaici.ui.favorites;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.VMVBFragment;
import com.github.lilinsong3.xiaobaici.databinding.FragmentFavoritesBinding;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoritesFragment extends VMVBFragment<FavoritesViewModel, FragmentFavoritesBinding> {

    private static final String TAG = "FavoritesFragment";

    @Override
    protected void setupViewData() {
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        viewBinding.toolbarFavorites.inflateMenu(R.menu.toolbar_favorites_menu);
        NavController navController = Navigation.findNavController(viewBinding.getRoot());
        NavigationUI.setupWithNavController(viewBinding.toolbarFavorites, navController);
        viewBinding.toolbarFavorites.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.favoriteEditFragment) {
                navController.navigate(FavoritesFragmentDirections.actionFavoritesToFavoriteEdit());
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        FavoriteListAdapter favoriteListAdapter = new FavoriteListAdapter(favoriteId ->
                NavHostFragment.findNavController(this).navigate(
                        FavoritesFragmentDirections.actionFavoritesToFavorite(favoriteId)
                )
        );
        viewBinding.recyclerFavorites.setAdapter(favoriteListAdapter);
        final MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(), RecyclerView.VERTICAL);
        divider.setLastItemDecorated(false);
        viewBinding.recyclerFavorites.addItemDecoration(divider);
        viewModel.favoriteModels.observe(getViewLifecycleOwner(), favoriteListAdapter::submitList);
    }

    @NonNull
    @Override
    protected Class<FavoritesViewModel> provideVMClass() {
        return FavoritesViewModel.class;
    }

    @Override
    public FragmentFavoritesBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentFavoritesBinding.inflate(inflater, container, false);
    }
}
