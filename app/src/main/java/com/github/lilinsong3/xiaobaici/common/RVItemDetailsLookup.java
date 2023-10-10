package com.github.lilinsong3.xiaobaici.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RVItemDetailsLookup<K> extends ItemDetailsLookup<K> {

    @NonNull
    protected final RecyclerView mRecyclerView;

    public RVItemDetailsLookup(@NonNull RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    // @Deprecated
//    @Nullable
//    @Override
//    public ItemDetails<K> getItemDetails(@NonNull MotionEvent e) {
//        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        // bug 记录
        // 场景：
        // 在 FavoriteFragment 界面的 RecyclerView 实现中出现：
        // 1. 点击任意一个item，无反应，或者说item没有被选择
        // 2. 全选后点击任意一个item，会导致所有选择被清除
        // 原因查找：
        // 打断点调试，在到 androidx.recyclerview.selection.TouchInputHandler#getItemDetails(MotionEvent)
        // 方法中，发现在处理单点手势时，会调用 ItemDetailsLookup#getItemDetails(MotionEvent) 方法，获取 ItemDetails，
        // 然而一直得到的是 null。场景中 SelectionTracker 用的 ItemDetailsLookup 是这里的。继续调试发现，在这里
        // mRecyclerView.getAdapter() 得到的是一个 ConcatAdapter，才想起来前几天刚给 PagingDataAdapter
        // 加了 footer，所以才会得到一个 ConcatAdapter，进而导致
        // ItemKeyConverter.class.isAssignableFrom(mRecyclerView.getAdapter().getClass())
        // 为假，从而返回 null。
//        if (view != null
//                && mRecyclerView.getAdapter() != null
//                && ItemKeyConverter.class.isAssignableFrom(mRecyclerView.getAdapter().getClass())
//        ) {
//            int position = mRecyclerView.getChildViewHolder(view).getBindingAdapterPosition();
//            return new ItemDetails<K>() {
//                @Override
//                public int getPosition() {
//                    return position;
//                }
//
//                @Nullable
//                @Override
//                public K getSelectionKey() {
//                    return ((ItemKeyConverter<K>) mRecyclerView.getAdapter()).getItemKeyBy(position);
//                }
//            };
//        }
//        return null;
//    }
}
