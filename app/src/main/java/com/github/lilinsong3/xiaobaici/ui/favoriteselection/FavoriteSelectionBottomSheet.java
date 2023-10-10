package com.github.lilinsong3.xiaobaici.ui.favoriteselection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;

import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.StableIdItemDetailsLookup;
import com.github.lilinsong3.xiaobaici.databinding.BottomSheetFavoriteSelectionBinding;
import com.github.lilinsong3.xiaobaici.util.RxUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.stream.StreamSupport;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

@AndroidEntryPoint
public class FavoriteSelectionBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "FavoriteSelectionBottomSheet";

    private BottomSheetFavoriteSelectionBinding binding;

    private SelectionTracker<Long> selectionTracker;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = BottomSheetFavoriteSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FavoriteSelectionViewModel favoriteSelectionViewModel = new ViewModelProvider(this).get(FavoriteSelectionViewModel.class);
        final FavoriteSelectionListAdapter adapter = new FavoriteSelectionListAdapter();
        binding.fsRecyclerFavorite.setAdapter(adapter);

        // 选择跟踪
        selectionTracker = new SelectionTracker.Builder<>(
                "favorite-selection",
                binding.fsRecyclerFavorite,
                new StableIdKeyProvider(binding.fsRecyclerFavorite),
                new StableIdItemDetailsLookup(binding.fsRecyclerFavorite),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build();

        adapter.setSelectionTracker(selectionTracker);

        // 点击按钮确认
        binding.fsBtnConfirm.setOnClickListener(v -> {
            favoriteSelectionViewModel.handleSelectFavorites(
                            StreamSupport.stream(
                                    selectionTracker.getSelection().spliterator(),
                                    false
                            ).toArray(Long[]::new)
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> Toast.makeText(requireContext(), R.string.long_operation_success, Toast.LENGTH_SHORT).show())
                    .doOnError(throwable -> Toast.makeText(requireContext(), R.string.long_operation_failed, Toast.LENGTH_SHORT).show())
                    .to(RxUtil.autoDispose(getViewLifecycleOwner()))
                    .subscribe();
            NavHostFragment.findNavController(this).popBackStack();
        });

        favoriteSelectionViewModel.models.observe(getViewLifecycleOwner(), adapter::submitList);

        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}