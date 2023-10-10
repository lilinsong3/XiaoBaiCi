package com.github.lilinsong3.xiaobaici.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class ModelDiffCallback<M> extends DiffUtil.ItemCallback<M>{
    @Override
    public boolean areItemsTheSame(@NonNull M oldItem, @NonNull M newItem) {
        return oldItem.hashCode() == newItem.hashCode();
    }

    @Override
    public boolean areContentsTheSame(@NonNull M oldItem, @NonNull M newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
