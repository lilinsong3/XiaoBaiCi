package com.github.lilinsong3.xiaobaici.common;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

public class UnstableIdItemDetailsLookup extends StableIdItemDetailsLookup {

    public UnstableIdItemDetailsLookup(@NonNull RecyclerView mRecyclerView) {
        super(mRecyclerView);
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) throws IllegalArgumentException {
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            int position = mRecyclerView.getChildViewHolder(view).getBindingAdapterPosition();
            return new ItemDetails<Long>() {
                @Override
                public int getPosition() {
                    return position;
                }

                @Nullable
                @Override
                public Long getSelectionKey() {
                    if (getSelectionKeyProvider().isPresent()) {
                        return (Long) getSelectionKeyProvider().get().getItemKey(position);
                    }
                    return null;
                }

                @Override
                public boolean inSelectionHotspot(@NonNull MotionEvent e) {
                    return UnstableIdItemDetailsLookup.super.getCanBeSelected().test(e);
                }
            };

        }
        return null;
    }

    @NonNull
    protected Optional<ItemDetailsProvider<?>> getSelectionKeyProvider() {
        RecyclerView.Adapter<?> adapter = mRecyclerView.getAdapter();
        if (adapter instanceof ItemDetailsProvider) {
            return Optional.of((ItemDetailsProvider<?>) mRecyclerView.getAdapter());
        }
        if (adapter instanceof ConcatAdapter) {
            ConcatAdapter concatAdapter = (ConcatAdapter) mRecyclerView.getAdapter();
            return concatAdapter.getAdapters()
                    .stream()
                    .filter(insideAdapter -> insideAdapter instanceof ItemDetailsProvider)
                    .findFirst()
                    .map(insideAdapter -> (ItemDetailsProvider<?>) insideAdapter);
        }
        return Optional.empty();
    }
}
