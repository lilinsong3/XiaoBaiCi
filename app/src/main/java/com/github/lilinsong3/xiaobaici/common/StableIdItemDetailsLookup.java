package com.github.lilinsong3.xiaobaici.common;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Predicate;

/**
 * 仅限于 hasStableIds 为 true 的 adapter
 */
public class StableIdItemDetailsLookup extends RVItemDetailsLookup<Long> {

    @NonNull
    private Predicate<MotionEvent> canBeSelected = e -> false;

    public StableIdItemDetailsLookup(@NonNull RecyclerView mRecyclerView) {
        super(mRecyclerView);
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(view);
            int position = viewHolder.getBindingAdapterPosition();

            return new ItemDetails<Long>() {
                @Override
                public int getPosition() {
                    return position;
                }

                @Nullable
                @Override
                public Long getSelectionKey() {
                    if (mRecyclerView.getAdapter() != null) {
                        return mRecyclerView.getAdapter().getItemId(position);
                    }
                    return null;
                }

                @Override
                public boolean inSelectionHotspot(@NonNull MotionEvent e) {
                    return canBeSelected.test(e);
                }
            };
        }
        return null;
    }

    @NonNull
    protected Predicate<MotionEvent> getCanBeSelected() {
        return canBeSelected;
    }

    public StableIdItemDetailsLookup withCanBeSelected(@NonNull Predicate<MotionEvent> canBeSelected) {
        setCanBeSelected(canBeSelected);
        return this;
    }

    public void setCanBeSelected(@NonNull Predicate<MotionEvent> canBeSelected) {
        this.canBeSelected = canBeSelected;
    }
}
