package com.github.lilinsong3.xiaobaici.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.databinding.ItemClearBinding;
import com.github.lilinsong3.xiaobaici.util.ViewUtil;

public class ItemClearAdapter extends RecyclerView.Adapter<ItemClearAdapter.ItemClearViewHolder> {

    @NonNull
    private final ViewUtil.OnClickAction onClickAction;

    public ItemClearAdapter(@NonNull ViewUtil.OnClickAction onClickAction) {
        this.onClickAction = onClickAction;
    }

    @NonNull
    @Override
    public ItemClearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemClearViewHolder(ItemClearBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemClearViewHolder holder, int position) {
        holder.bind(onClickAction);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ItemClearViewHolder extends RecyclerView.ViewHolder {
        public ItemClearViewHolder(@NonNull ItemClearBinding binding) {
            super(binding.getRoot());
        }

        public void bind(@NonNull ViewUtil.OnClickAction onClickAction) {
            itemView.setOnClickListener(v -> onClickAction.onClick());
        }
    }
}
