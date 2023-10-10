package com.github.lilinsong3.xiaobaici.ui.histories;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.common.ModelDiffCallback;
import com.github.lilinsong3.xiaobaici.data.model.BrowsingHistoryModel;
import com.github.lilinsong3.xiaobaici.databinding.ItemBrowsingHistoryBinding;
import com.github.lilinsong3.xiaobaici.util.ViewUtil;

public class BrowsingHistoryPageAdapter extends PagingDataAdapter<BrowsingHistoryModel, BrowsingHistoryPageAdapter.BrowsingHistoryViewHolder> {

    @NonNull
    private final ViewUtil.OnItemClickListener<Long> onDeleteIconClickListener;
    @NonNull
    private final ViewUtil.OnItemClickListener<Long> onItemClickListener;

    public BrowsingHistoryPageAdapter(
            @NonNull ViewUtil.OnItemClickListener<Long> onDeleteIconClickListener,
            @NonNull ViewUtil.OnItemClickListener<Long> onItemClickListener
    ) {
        super(new ModelDiffCallback<>());
        this.onDeleteIconClickListener = onDeleteIconClickListener;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BrowsingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BrowsingHistoryViewHolder(ItemBrowsingHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BrowsingHistoryViewHolder holder, int position) {
        holder.bind(getItem(position), onDeleteIconClickListener, onItemClickListener);
    }

    public static final class BrowsingHistoryViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ItemBrowsingHistoryBinding viewBinding;

        public BrowsingHistoryViewHolder(@NonNull ItemBrowsingHistoryBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }

        public void bind(
                @Nullable BrowsingHistoryModel model,
                @NonNull ViewUtil.OnItemClickListener<Long> onDeleteIconClickListener,
                @NonNull ViewUtil.OnItemClickListener<Long> onItemClickListener
        ) {
            if (model != null) {
                viewBinding.historyTextSubject.setText(model.wordSubject);
                viewBinding.historyTextDatetime.setText(model.datetime);
                viewBinding.historyIconDelete.setOnClickListener(v -> onDeleteIconClickListener.onClick(model.hanziWordId));
                itemView.setOnClickListener(v -> onItemClickListener.onClick(model.hanziWordId));
            }
        }
    }
}
