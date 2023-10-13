package com.github.lilinsong3.xiaobaici.ui.favorite;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;

import com.github.lilinsong3.xiaobaici.MainActivityViewModel;
import com.github.lilinsong3.xiaobaici.NavGraphDirections;
import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.ItemLoadStateAdapter;
import com.github.lilinsong3.xiaobaici.common.RVItemKeyProvider;
import com.github.lilinsong3.xiaobaici.common.UnstableIdItemDetailsLookup;
import com.github.lilinsong3.xiaobaici.common.VMVBFragment;
import com.github.lilinsong3.xiaobaici.databinding.FragmentFavoriteBinding;
import com.github.lilinsong3.xiaobaici.util.RxUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import kotlin.Unit;

@AndroidEntryPoint
public class FavoriteFragment extends VMVBFragment<FavoriteViewModel, FragmentFavoriteBinding> {

    private FavoriteHanziPageAdapter adapter;
    private SelectionTracker<Long> selectionTracker;

    private NavController navController;

    private LifecycleOwner lifecycleOwner;

    private UnstableIdItemDetailsLookup itemDetailsLookup;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void setupViewData() {
        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        lifecycleOwner = getViewLifecycleOwner();
        setupToolbar();
        setupHeaderFavorite();

        viewModel.loading.observe(lifecycleOwner, mainActivityViewModel::setGlobalLoading);

        setupSelectionView();
    }

    private void setupToolbar() {
        viewBinding.favoriteToolbar.inflateMenu(R.menu.toolbar_favorite_menu);
        navController = Navigation.findNavController(viewBinding.getRoot());
        NavigationUI.setupWithNavController(viewBinding.favoriteToolbar, navController);

        viewBinding.favoriteToolbar.setOnMenuItemClickListener(menuItem -> {
            Integer itemId = menuItem.getItemId();
            // 进入批处理
            if (itemId.equals(R.id.mi_favorite_batch)) {
                viewModel.setBatchMode(true);
                return true;
            }

            // 取消批处理
            if (itemId.equals(R.id.mi_favorite_batch_cancel)) {
                viewModel.setBatchMode(false);
                return true;
            }
            return false;
        });
    }

    private void setupHeaderFavorite() {
        // 点击删除
        viewBinding.favoriteTextDelete.setOnClickListener(v -> openConfirmDeleteDialog());

        // 点击编辑
        viewBinding.favoriteTextEdit.setOnClickListener(Navigation.createNavigateOnClickListener(
                FavoriteFragmentDirections.actionFavoriteToFavoriteEdit().setFavoriteId(viewModel.argFavoriteId)
        ));

        viewModel.favoriteModel.observe(lifecycleOwner, favoriteModel -> {
            viewBinding.favoriteTextName.setText(favoriteModel.name);
            if (!favoriteModel.isDefault) {
                viewBinding.favoriteTextTag.setVisibility(View.GONE);
            }
            viewBinding.favoriteTextHanziCount.setText(getString(R.string.long_hanzi_word_count, favoriteModel.hanziWordCount));
        });
    }

    private void openConfirmDeleteDialog() {
        final Context context = requireContext();
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.short_delete_favorite)
                .setMessage(R.string.long_confirm_delete_favorite)
                .setNegativeButton(R.string.short_cancel, (dialog, which) -> dialog.cancel())
                .setPositiveButton(R.string.short_sure, (dialog, which) -> {
                    dialog.dismiss();
                    // 删除，回到上一层界面
                    viewModel.doDelete()
                            .observeOn(AndroidSchedulers.mainThread())
                            .to(RxUtil.autoDispose(lifecycleOwner))
                            .subscribe(
                                    navController::popBackStack,
                                    e -> Toast.makeText(context, R.string.long_operation_failed, Toast.LENGTH_SHORT)
                            );
                }).show();
    }

    private void setupSelectionView() {
        if (adapter == null) {
            adapter = new FavoriteHanziPageAdapter(
                    hanziWordId -> navController.navigate(NavGraphDirections.actionGlobalHanziWord(hanziWordId)),
                    (menuItem, hanziWordId) -> {
                        Integer menuItemId = menuItem.getItemId();
                        // 点击取消收藏
                        if (menuItemId.equals(R.id.mi_fh_cancel)) {
                            viewModel.cancelFavorite(hanziWordId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnError(throwable -> Toast.makeText(requireContext(), R.string.long_operation_failed, Toast.LENGTH_SHORT).show())
                                    .to(RxUtil.autoDispose(lifecycleOwner))
                                    .subscribe();
                            return true;
                        }
                        // 点击选择收藏夹
                        // 先加载所有收藏了被点击的词语的收藏夹，再弹出BottomSheet
                        if (menuItemId.equals(R.id.mi_fh_select)) {
                            navController.navigate(NavGraphDirections.actionGlobalFavoriteSelection().setHanziWordId(hanziWordId));
                            return true;
                        }
                        return false;
                    }
            );
        }

        adapter.addOnPagesUpdatedListener(() -> {
            viewModel.setPageLoading(false);
            return Unit.INSTANCE;
        });
        viewBinding.favoriteRecyclerHanzi.setAdapter(adapter.withLoadStateFooter(new ItemLoadStateAdapter(adapter::retry)));

        // 选择跟踪
        itemDetailsLookup = new UnstableIdItemDetailsLookup(viewBinding.favoriteRecyclerHanzi);
        selectionTracker = new SelectionTracker.Builder<>(
                "favorite-selection",
                viewBinding.favoriteRecyclerHanzi,
                new RVItemKeyProvider<>(adapter),
                itemDetailsLookup,
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build();
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                // 设置批量处理模式
                if (selectionTracker.hasSelection()) {
                    viewModel.setBatchMode(true);
                }

                int selectionNumber = selectionTracker.getSelection().size();
                // 全选CheckBox跟随状态
                viewBinding.favoriteCbSelectAll.setChecked(selectionNumber == adapter.snapshot().getItems().size());

                // 显示选择数量
                viewBinding.favoriteTextSelectedCount.setText(getString(R.string.long_selected_count, selectionNumber));
            }
        });

        adapter.setSelectionTracker(selectionTracker);

        // 点击全选CheckBox
        viewBinding.favoriteCbSelectAll.setOnClickListener(v -> selectionTracker.setItemsSelected(
                // keys of current items
                adapter.snapshot().getItems().stream().map(model -> model.hanziWordId).collect(Collectors.toList()),
                viewBinding.favoriteCbSelectAll.isChecked())
        );

        // 显示初始选择数量
        viewBinding.favoriteTextSelectedCount.setText(getString(R.string.long_selected_count, 0));

        // 批量取消收藏
        viewBinding.favoriteBtnRemove.setOnClickListener(v -> {
            if (selectionTracker.hasSelection()) {
                viewModel.cancelFavorite(StreamSupport.stream(
                                selectionTracker.getSelection().spliterator(),
                                false
                        ).collect(Collectors.toList()))
                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnComplete(() -> viewModel.setBatchMode(false))
                        .doOnError(throwable -> Toast.makeText(requireContext(), R.string.long_operation_failed, Toast.LENGTH_SHORT).show())
                        .to(RxUtil.autoDispose(lifecycleOwner))
                        .subscribe();
            } else {
                Toast.makeText(requireContext(), R.string.long_select_words, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.pagingFavoriteHanziModels.observe(lifecycleOwner, pagingData -> adapter.submitData(lifecycleOwner.getLifecycle(), pagingData));
        // 切换批处理模式
        viewModel.distinctBatchMode.observe(lifecycleOwner, this::switchBatchModeView);
    }

    private void switchBatchModeView(boolean switchBatchModeOn) {
        // all items can be selected or not
        itemDetailsLookup.setCanBeSelected(e -> switchBatchModeOn);

        final Menu menu = viewBinding.favoriteToolbar.getMenu();
        //  show mi_favorite_batch and mi_favorite_batch_cancel or not
        menu.findItem(R.id.mi_favorite_batch).setEnabled(!switchBatchModeOn).setVisible(!switchBatchModeOn);
        menu.findItem(R.id.mi_favorite_batch_cancel).setEnabled(switchBatchModeOn).setVisible(switchBatchModeOn);

        // show the batch div at the bottom or not
        viewBinding.favoriteBottomDiv.setVisibility(switchBatchModeOn ? View.VISIBLE : View.GONE);

        // if batch mode is off
        if (!switchBatchModeOn) {
            selectionTracker.clearSelection();
        }
    }

    @NonNull
    @Override
    protected Class<FavoriteViewModel> provideVMClass() {
        return FavoriteViewModel.class;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
    }

    @Override
    public FragmentFavoriteBinding initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentFavoriteBinding.inflate(inflater, container, false);
    }
}