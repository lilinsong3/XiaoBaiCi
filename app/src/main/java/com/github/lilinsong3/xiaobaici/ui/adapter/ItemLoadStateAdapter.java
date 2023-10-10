package com.github.lilinsong3.xiaobaici.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.github.lilinsong3.xiaobaici.R;
import com.github.lilinsong3.xiaobaici.databinding.ViewErrorRetryBinding;
import com.github.lilinsong3.xiaobaici.databinding.ViewHintBinding;
import com.github.lilinsong3.xiaobaici.databinding.ViewLoadingBinding;

public class ItemLoadStateAdapter extends androidx.paging.LoadStateAdapter<ItemLoadStateAdapter.LoadStateViewHolder<? extends ViewBinding>> {

    private static final String TAG = "ItemLoadStateAdapter";
    private final RetryAction retryAction;

    public ItemLoadStateAdapter(RetryAction retryAction) {
        this.retryAction = retryAction;
    }

    @Override
    public void onBindViewHolder(@NonNull LoadStateViewHolder<? extends ViewBinding> loadStateViewHolder, @NonNull LoadState loadState) {
        loadStateViewHolder.bindLoadState(loadState);
    }

    @NonNull
    @Override
    public LoadStateViewHolder<? extends ViewBinding> onCreateViewHolder(@NonNull ViewGroup viewGroup, @NonNull LoadState loadState) {
        LoadStateViewType loadStateViewType = LoadStateViewType.getLoadStateViewType(loadState);
        switch (loadStateViewType) {
            case LOADING:
                return new LoadingStateViewHolder(viewGroup);
            case NO_MORE:
                return new NotLoadingStateViewHolder(viewGroup, true);
            case HAS_MORE:
//                if (dataCount == 0) {
//                    return new LoadingStateViewHolder(viewGroup);
//                }
                return new NotLoadingStateViewHolder(viewGroup, false);
            default:
                return new ErrorStateViewHolder(viewGroup, retryAction);
        }
    }

    /**
     * 测试时发现，这个适配器作为页脚时，如果直接返回 true，会发生加载完所有数据并且滚动展示到页脚的情况
     * 考虑到 PagingData 是被预加载的，所以如果是 LoadStateViewType.HAS_MORE 状态的话，这个状态适配器作为页脚是很难显示出来的
     * @param loadState PagingSource 加载状态
     * @return 是否将当前加载状态作为 adapter 的一项显示在 UI 上
     */
    @Override
    public boolean displayLoadStateAsItem(@NonNull LoadState loadState) {
        return LoadStateViewType.getLoadStateViewType(loadState) != LoadStateViewType.HAS_MORE;
    }

    @Override
    public int getStateViewType(@NonNull LoadState loadState) {
        return LoadStateViewType.getViewTypeValue(loadState);
    }

    @FunctionalInterface
    public interface RetryAction {
        void retry();
    }

    public enum LoadStateViewType {
        LOADING,
        NO_MORE,
        HAS_MORE,
        ERROR;

        public static int getViewTypeValue(@NonNull LoadState loadState) {
            return getLoadStateViewType(loadState).ordinal();
        }

        public static LoadStateViewType getLoadStateViewType(@NonNull LoadState loadState) {
            if (loadState instanceof  LoadState.Loading) {
                return LOADING;
            }
            if (loadState instanceof LoadState.NotLoading) {
                return loadState.getEndOfPaginationReached() ? NO_MORE : HAS_MORE;
            }
            return ERROR;
        }
    }

    public static abstract class LoadStateViewHolder<B extends ViewBinding> extends RecyclerView.ViewHolder {

        protected final B binding;

        public LoadStateViewHolder(@NonNull B binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public abstract void bindLoadState(LoadState loadState);
    }

    public static class LoadingStateViewHolder extends LoadStateViewHolder<ViewLoadingBinding> {

        public LoadingStateViewHolder(@NonNull ViewGroup parent) {
            super(ViewLoadingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void bindLoadState(LoadState loadState) {
            binding.circularLoading.setVisibilityAfterHide(View.GONE);
            binding.circularLoading.show();
//            // 加载与否
//            if (loadState instanceof LoadState.Loading) {
//                binding.progressLoading.show();
//            } else {
//                binding.progressLoading.hide();
//            }
        }
    }

    public static class ErrorStateViewHolder extends LoadStateViewHolder<ViewErrorRetryBinding> {
        private final @NonNull RetryAction retryAction;

        public ErrorStateViewHolder(@NonNull ViewGroup parent, @NonNull RetryAction retryAction) {
            super(ViewErrorRetryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            this.retryAction = retryAction;
        }

        @Override
        public void bindLoadState(LoadState loadState) {
            binding.viewBtnRetry.setOnClickListener(v -> retryAction.retry());
        }
    }

    public static class NotLoadingStateViewHolder extends LoadStateViewHolder<ViewHintBinding> {

        private final boolean toTheEnd;

        public NotLoadingStateViewHolder(@NonNull ViewGroup parent, boolean toTheEnd) {
            super(ViewHintBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            this.toTheEnd = toTheEnd;
        }

        @Override
        public void bindLoadState(LoadState loadState) {
            @StringRes int hintStringInt = R.string.long_scroll_down_more;
            if (toTheEnd) {
                hintStringInt = R.string.long_bottom;
            }
            binding.viewTextHint.setText(itemView.getContext().getString(hintStringInt));
        }
    }
}
