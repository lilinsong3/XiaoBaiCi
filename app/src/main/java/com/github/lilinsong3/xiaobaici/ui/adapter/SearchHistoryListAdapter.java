package com.github.lilinsong3.xiaobaici.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.data.model.SearchHistoryModel;
import com.github.lilinsong3.xiaobaici.databinding.ItemSearchHistoryBinding;
import com.github.lilinsong3.xiaobaici.util.ViewUtil;

public class SearchHistoryListAdapter extends ListAdapter<SearchHistoryModel, SearchHistoryListAdapter.SearchHistoryViewHolder> {

    private final @Nullable ViewUtil.OnItemClickListener<String> onItemClickListener;
    private final @Nullable ViewUtil.OnItemClickListener<Long> onDeleteClickListener;

    public SearchHistoryListAdapter(
            @Nullable ViewUtil.OnItemClickListener<String> onItemClickListener,
            @Nullable ViewUtil.OnItemClickListener<Long> onDeleteClickListener
    ) {
        super(new SearchHistoryModelDiffCallback());
        this.onItemClickListener = onItemClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchHistoryViewHolder(ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, int position) {
        holder.bind(getItem(position), onItemClickListener, onDeleteClickListener);
    }

    public static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemSearchHistoryBinding binding;

        public SearchHistoryViewHolder(@NonNull ItemSearchHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(
                @NonNull SearchHistoryModel model,
                @Nullable ViewUtil.OnItemClickListener<String> onItemClickListener,
                @Nullable ViewUtil.OnItemClickListener<Long> onDeleteClickListener
        ) {
            binding.iShTextSubject.setText(model.word);
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(model.word);
                }
            });
            binding.iShIconDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onClick(model.id);
                }
            });
        }
    }

    public static final class SearchHistoryModelDiffCallback extends DiffUtil.ItemCallback<SearchHistoryModel> {

        @Override
        public boolean areItemsTheSame(@NonNull SearchHistoryModel oldItem, @NonNull SearchHistoryModel newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchHistoryModel oldItem, @NonNull SearchHistoryModel newItem) {
            return oldItem.word.equals(newItem.word);
        }
    }
}
