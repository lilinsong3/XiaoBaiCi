package com.github.lilinsong3.xiaobaici.ui.favorites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.common.DefaultDiffItemCallback;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteModel;
import com.github.lilinsong3.xiaobaici.databinding.ItemFavoriteNavBinding;
import com.github.lilinsong3.xiaobaici.util.ViewUtil;

public class FavoriteListAdapter extends ListAdapter<FavoriteModel, FavoriteListAdapter.FavoriteViewHolder> {

    @NonNull
    private final ViewUtil.OnItemClickListener<Long> onFavoriteItemClickListener;

    protected FavoriteListAdapter(@NonNull ViewUtil.OnItemClickListener<Long> onFavoriteItemClickListener) {
        super(new DefaultDiffItemCallback<>());
        this.onFavoriteItemClickListener = onFavoriteItemClickListener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteViewHolder(ItemFavoriteNavBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(getItem(position), onFavoriteItemClickListener);
    }

    public static final class FavoriteViewHolder extends RecyclerView.ViewHolder {

        private final ItemFavoriteNavBinding viewBinding;

        public FavoriteViewHolder(@NonNull ItemFavoriteNavBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }

        public void bind(@NonNull FavoriteModel model, @NonNull ViewUtil.OnItemClickListener<Long> onFavoriteItemClickListener) {
            viewBinding.textNavFavoriteName.setText(model.name);
            if (!model.isDefault) {
                viewBinding.textNavFavoriteTag.setVisibility(View.GONE);
            }
            viewBinding.textNavFavoriteCount.setText(itemView.getContext().getString(R.string.long_hanzi_word_count, model.hanziWordCount));
            itemView.setOnClickListener(v -> onFavoriteItemClickListener.onClick(model.id));
        }
    }
}
