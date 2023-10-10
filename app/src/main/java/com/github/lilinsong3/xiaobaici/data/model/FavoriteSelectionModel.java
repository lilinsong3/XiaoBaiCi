package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FavoriteSelectionModel {

    @NonNull
    public final Long id;
    @NonNull
    public final String name;
    @NonNull
    public final Boolean isDefault;
    @NonNull
    public final Integer hanziWordCount;
    @NonNull
    public final Boolean selected;

    public FavoriteSelectionModel(
            @NonNull Long id,
            @NonNull String name,
            @NonNull Boolean isDefault,
            @NonNull Integer hanziWordCount,
            @NonNull Boolean selected
    ) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.hanziWordCount = hanziWordCount;
        this.selected = selected;
    }

    public FavoriteSelectionModel(@NonNull FavoriteModel favoriteModel, @NonNull Boolean selected) {
        this(
                favoriteModel.id,
                favoriteModel.name,
                favoriteModel.isDefault,
                favoriteModel.hanziWordCount,
                selected
        );
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof FavoriteSelectionModel) {
            FavoriteSelectionModel model = (FavoriteSelectionModel) obj;
            return this.id.equals(model.id)
                    && this.name.equals(model.name)
                    && this.isDefault.equals(model.isDefault)
                    && this.hanziWordCount.equals(model.hanziWordCount)
                    && this.selected.equals(model.selected);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

}
