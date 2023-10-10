package com.github.lilinsong3.xiaobaici.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

public class RVItemKeyProvider<K> extends ItemKeyProvider<K> {

    private static final String TAG = "RVItemKeyProvider";

    @NonNull
    private final ItemDetailsProvider<K> itemDetailsProvider;

    /**
     * Creates a new provider with the given scope.
     *
     * @param scope     Scope can't be changed at runtime.
     * @param itemDetailsProvider converter that convert id and position of item to each other
     */
    public RVItemKeyProvider(@Scope int scope, @NonNull ItemDetailsProvider<K> itemDetailsProvider) {
        super(scope);
        this.itemDetailsProvider = itemDetailsProvider;
    }

    public RVItemKeyProvider(@NonNull ItemDetailsProvider<K> itemDetailsProvider) {
        this(ItemKeyProvider.SCOPE_MAPPED, itemDetailsProvider);
    }


    @Nullable
    @Override
    public K getKey(int position) {
        return itemDetailsProvider.getItemKey(position);
    }

    @Override
    public int getPosition(@NonNull K key) {
        return itemDetailsProvider.getItemPosition(key);
    }
}
