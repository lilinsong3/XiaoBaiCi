package com.github.lilinsong3.xiaobaici.ui.my;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.databinding.ItemMyNavMenuBinding;

import java.util.List;

public final class MyNavMenuAdapter extends RecyclerView.Adapter<MyNavMenuAdapter.MyNavMenuItemViewHolder> {

    private static final String TAG = "MyNavMenuAdapter";

    @NonNull
    private final List<MyNavMenuItem> items;

    public MyNavMenuAdapter(@NonNull List<MyNavMenuItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MyNavMenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyNavMenuItemViewHolder(ItemMyNavMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyNavMenuItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static final class MyNavMenuItemViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemMyNavMenuBinding binding;

        public MyNavMenuItemViewHolder(@NonNull ItemMyNavMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MyNavMenuItem item) {
            binding.iIvHead.setImageDrawable(item.icon);
            binding.iTextTitle.setText(item.title);
            binding.getRoot().setOnClickListener(item.onClickListener);
        }
    }

    public static class MyNavMenuItem {
        public final Drawable icon;
        public final String title;
        public final View.OnClickListener onClickListener;

        public MyNavMenuItem(Drawable icon, String title, View.OnClickListener onClickListener) {
            this.icon = icon;
            this.title = title;
            this.onClickListener = onClickListener;
        }
    }
}
