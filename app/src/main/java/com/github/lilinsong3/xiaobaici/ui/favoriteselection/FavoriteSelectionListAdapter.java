package com.github.lilinsong3.xiaobaici.ui.favoriteselection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.DefaultDiffItemCallback;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteSelectionModel;
import com.github.lilinsong3.xiaobaici.databinding.ItemFavoriteSelectableBinding;

import java.util.List;
import java.util.stream.Collectors;

public final class FavoriteSelectionListAdapter
        extends ListAdapter<FavoriteSelectionModel, FavoriteSelectionListAdapter.FavoriteItemViewHolder>
{
    private static final String TAG = "FavoriteSelectionListAdapter";

    @Nullable
    private SelectionTracker<Long> selectionTracker;

    public FavoriteSelectionListAdapter() {
        super(new DefaultDiffItemCallback<>());
        setHasStableIds(true);
    }

    public void setSelectionTracker(@NonNull SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @Override
    public void onCurrentListChanged(@NonNull List<FavoriteSelectionModel> previousList, @NonNull List<FavoriteSelectionModel> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        if (selectionTracker != null) {
            selectionTracker.setItemsSelected(currentList.stream()
                    .filter(favoriteSelectionModel -> favoriteSelectionModel.selected)
                    .map(favoriteSelectionModel -> favoriteSelectionModel.id)
                    .collect(Collectors.toList()),
                    true
            );
        }
    }

    @NonNull
    @Override
    public FavoriteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteItemViewHolder(ItemFavoriteSelectableBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteItemViewHolder holder, int position) {
        FavoriteSelectionModel model = getItem(position);
        holder.bind(model, selectionTracker != null && selectionTracker.isSelected(model.id));
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public static final class FavoriteItemViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FavoriteItemViewHolder";

        private final @NonNull ItemFavoriteSelectableBinding binding;

        public FavoriteItemViewHolder(@NonNull ItemFavoriteSelectableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull FavoriteSelectionModel model, @NonNull Boolean isSelected) {
            binding.textSelectiveFavoriteName.setText(model.name);
            if (!model.isDefault) {
                binding.textSelectiveFavoriteTag.setVisibility(View.GONE);
            }
            binding.textSelectiveFavoriteCount.setText(itemView.getContext().getString(R.string.long_hanzi_word_count, model.hanziWordCount));
            binding.cbSelectiveFavorite.setChecked(isSelected);
        }
    }
}
