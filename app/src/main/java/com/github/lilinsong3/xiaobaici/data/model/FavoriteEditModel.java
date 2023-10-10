package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.NonNull;

public class FavoriteEditModel {
    @NonNull
    public final Long id;

    @NonNull
    public final String name;

    @NonNull
    public final Boolean isDefault;

    public FavoriteEditModel(@NonNull Long id, @NonNull String name, @NonNull Boolean isDefault) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
    }
}
