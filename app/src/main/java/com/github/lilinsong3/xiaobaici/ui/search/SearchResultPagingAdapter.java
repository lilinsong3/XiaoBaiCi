package com.github.lilinsong3.xiaobaici.ui.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.data.model.HanziWordModel;
import com.github.lilinsong3.xiaobaici.databinding.ItemWordCardBinding;
import com.github.lilinsong3.xiaobaici.util.StringUtil;
import com.github.lilinsong3.xiaobaici.util.ViewUtil;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;

import static com.github.lilinsong3.xiaobaici.util.ViewUtil.showTextIfNonNull;

// HanziWordModel.favoriteCount 多余
public class SearchResultPagingAdapter extends PagingDataAdapter<HanziWordModel, SearchResultPagingAdapter.SearchResultViewHolder> {
    private final @NonNull ViewUtil.OnItemClickListener<Long> onResultItemClickListener;

    private String searchWord = "";

    public SearchResultPagingAdapter(
            @NonNull ViewUtil.OnItemClickListener<Long> onResultItemClickListener
    ) {
        super(new HanziWordModelDiffCallback());
        this.onResultItemClickListener = onResultItemClickListener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchResultViewHolder(ItemWordCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        HanziWordModel model = getItem(position);
        if (model != null) {
            holder.bind(model, onResultItemClickListener, searchWord);
        }
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private final ItemWordCardBinding binding;

        public SearchResultViewHolder(@NonNull ItemWordCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(
                HanziWordModel model,
                @NonNull ViewUtil.OnItemClickListener<Long> onResultItemClickListener,
                String searchWord
        ) {
            int highlightColor = MaterialColors.getColor(itemView, R.attr.colorError);
            binding.wordItemTextSubject.setText(StringUtil.highlightString(model.subject, searchWord, highlightColor));
            showTextIfNonNull(binding.wordItemTextMeaning, StringUtil.highlightString(model.meaning, searchWord, highlightColor));
            showTextIfNonNull(binding.wordItemTextUsage, StringUtil.highlightString(model.usage, searchWord, highlightColor));
            showTextIfNonNull(binding.wordItemTextMeaning, StringUtil.highlightString(model.example, searchWord, highlightColor));
            binding.getRoot().setOnClickListener(v -> onResultItemClickListener.onClick(model.id));
        }
    }

    public static class HanziWordModelDiffCallback extends DiffUtil.ItemCallback<HanziWordModel> {
        @Override
        public boolean areItemsTheSame(@NonNull HanziWordModel oldItem, @NonNull HanziWordModel newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull HanziWordModel oldItem, @NonNull HanziWordModel newItem) {
            return oldItem.subject.equals(newItem.subject);
        }
    }
}
