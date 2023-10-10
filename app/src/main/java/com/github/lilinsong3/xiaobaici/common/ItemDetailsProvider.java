package com.github.lilinsong3.xiaobaici.common;

public interface ItemDetailsProvider<K> {
    K getItemKey(int position);
    int getItemPosition(K itemKey);
}
