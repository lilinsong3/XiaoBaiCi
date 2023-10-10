package com.github.lilinsong3.xiaobaici.ui.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lilinsong3.xiaobaici.data.model.SearchSuggestionModel;
import com.github.lilinsong3.xiaobaici.databinding.ItemSearchSuggestionBinding;
import com.github.lilinsong3.xiaobaici.util.StringUtil;
import com.github.lilinsong3.xiaobaici.util.ViewUtil;
import com.google.android.material.color.MaterialColors;

public class SearchSuggestionListAdapter extends ListAdapter<SearchSuggestionModel, SearchSuggestionListAdapter.SearchSuggestionModelViewHolder> {

    private static final String TAG = "sSuggestionListAdapter";

    private final ViewUtil.OnItemClickListener<String> onItemClickListener;
    private String liveInputKeyword = "";

    public SearchSuggestionListAdapter(@NonNull ViewUtil.OnItemClickListener<String> onItemClickListener) {
        super(new SearchSuggestionModelDiffCallback());
        this.onItemClickListener = onItemClickListener;
    }


    public void setLiveInputKeyword(String liveInputKeyword) {
        this.liveInputKeyword = liveInputKeyword;
    }

    @NonNull
    @Override
    public SearchSuggestionModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchSuggestionModelViewHolder(ItemSearchSuggestionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSuggestionModelViewHolder holder, int position) {
        SearchSuggestionModel model = getItem(position);
        if (model != null) {
            holder.bind(model, onItemClickListener, liveInputKeyword);
        }
    }

    public static class SearchSuggestionModelViewHolder extends RecyclerView.ViewHolder {
        private final ItemSearchSuggestionBinding binding;
        public SearchSuggestionModelViewHolder(@NonNull ItemSearchSuggestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(
                @NonNull SearchSuggestionModel model,
                @NonNull ViewUtil.OnItemClickListener<String> onItemClickListener,
                String keyword
        ) {
            binding.textSuggestion.setText(StringUtil.highlightString(model.suggestion, keyword, MaterialColors.getColor(itemView, com.google.android.material.R.attr.colorError)));
            binding.textSuggestion.setOnClickListener(v -> onItemClickListener.onClick(model.suggestion));
        }
    }

    public static class SearchSuggestionModelDiffCallback extends DiffUtil.ItemCallback<SearchSuggestionModel> {

        @Override
        public boolean areItemsTheSame(@NonNull SearchSuggestionModel oldItem, @NonNull SearchSuggestionModel newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchSuggestionModel oldItem, @NonNull SearchSuggestionModel newItem) {
            return oldItem.suggestion.equals(newItem.suggestion);
        }
    }
}
