package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.lilinsong3.xiaobaici.data.local.entities.Favorite;

public class FavoriteModel {
    @NonNull
    public final Long id;

    @NonNull
    public final String name;

    @NonNull
    public final Boolean isDefault;

    @NonNull
    public final Integer hanziWordCount;

    public FavoriteModel(
            @NonNull Long id,
            @NonNull String name,
            @NonNull Boolean isDefault,
            @NonNull Integer hanziWordCount
    ) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.hanziWordCount = hanziWordCount;
    }

    public static FavoriteModel from(Favorite favorite) {
        return new FavoriteModel(favorite.id, favorite.name, favorite.isDefault, 0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof FavoriteModel) {
            FavoriteModel model = (FavoriteModel) obj;
            return name.equals(model.name)
                    && isDefault.equals(model.isDefault)
                    && hanziWordCount.equals(model.hanziWordCount);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }
}
