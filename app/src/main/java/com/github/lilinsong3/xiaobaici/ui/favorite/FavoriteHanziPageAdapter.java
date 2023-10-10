package com.github.lilinsong3.xiaobaici.ui.favorite;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.ItemDetailsProvider;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteHanziModel;
import com.github.lilinsong3.xiaobaici.databinding.ItemFavoriteHanziBinding;
import com.github.lilinsong3.xiaobaici.util.ViewUtil;

import java.util.List;

public class FavoriteHanziPageAdapter
        extends PagingDataAdapter<FavoriteHanziModel, FavoriteHanziPageAdapter.FavoriteHanziViewHolder>
    implements ItemDetailsProvider<Long>
{

    @NonNull
    private final ViewUtil.OnItemClickListener<Long> onItemClickListener;
    @NonNull
    private final OnMoreMenuItemClickListener onMoreMenuItemClickListener;

    @Nullable
    private SelectionTracker<Long> selectionTracker;

    public FavoriteHanziPageAdapter(
            @NonNull ViewUtil.OnItemClickListener<Long> onItemClickListener,
            @NonNull OnMoreMenuItemClickListener onMoreMenuItemClickListener
    ) {
        super(new FavoriteHanziModelDiffCallback());
        this.onItemClickListener = onItemClickListener;
        this.onMoreMenuItemClickListener = onMoreMenuItemClickListener;
    }

    public void setSelectionTracker(@Nullable SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public FavoriteHanziViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteHanziViewHolder(
                ItemFavoriteHanziBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHanziViewHolder holder, int position) {
        FavoriteHanziModel model = getItem(position);
        holder.itemView.setOnClickListener(v -> {
            if (model != null) {
                onItemClickListener.onClick(model.hanziWordId);
            }
        });
        holder.bind(
                model,
                onMoreMenuItemClickListener,
                selectionTracker != null && selectionTracker.isSelected(model == null ? RecyclerView.NO_ID : model.hanziWordId)
        );
    }

    @Override
    public Long getItemKey(int position) {
        final FavoriteHanziModel model = getItem(position);
        return model == null ? RecyclerView.NO_ID : model.hanziWordId;
    }

    @Override
    public int getItemPosition(@NonNull Long key) {
        List<FavoriteHanziModel> models = snapshot();
        for (FavoriteHanziModel model : models) {
            if (model.hanziWordId.equals(key)) {
                return models.indexOf(model);
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public static final class FavoriteHanziViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemFavoriteHanziBinding viewBinding;

        @NonNull
        private final PopupMenu popupMenu;

        public FavoriteHanziViewHolder(@NonNull ItemFavoriteHanziBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            popupMenu = new PopupMenu(itemView.getContext(), viewBinding.fhIconMore);
            popupMenu.getMenuInflater().inflate(R.menu.favotite_hanzi_popup_menu, popupMenu.getMenu());
            viewBinding.fhIconMore.setOnClickListener(v -> popupMenu.show());
        }

        public void bind(
                @Nullable FavoriteHanziModel model,
                @NonNull OnMoreMenuItemClickListener onMoreMenuItemClickListener,
                @NonNull Boolean isSelected
        ) {
            itemView.setActivated(isSelected);
            if (model != null) {
                viewBinding.fhTextSubject.setText(model.subject);
                viewBinding.fhTextDatetime.setText(model.datetime);
                popupMenu.setOnMenuItemClickListener(menuItem -> onMoreMenuItemClickListener.onClick(menuItem, model.hanziWordId));
            }
        }
    }

    @FunctionalInterface
    public interface OnMoreMenuItemClickListener {
        boolean onClick(@NonNull MenuItem menuItem, @NonNull Long hanziWordId);
    }

    public static final class FavoriteHanziModelDiffCallback extends DiffUtil.ItemCallback<FavoriteHanziModel> {

        @Override
        public boolean areItemsTheSame(@NonNull FavoriteHanziModel oldItem, @NonNull FavoriteHanziModel newItem) {
            return oldItem.hanziWordId.equals(newItem.hanziWordId);
        }

        @Override
        public boolean areContentsTheSame(@NonNull FavoriteHanziModel oldItem, @NonNull FavoriteHanziModel newItem) {
            return oldItem.subject.equals(newItem.subject)
                    && oldItem.datetime.equals(newItem.datetime);
        }
    }
}
