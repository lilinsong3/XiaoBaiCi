package com.github.lilinsong3.xiaobaici.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"hanziWordId", "favoriteId"})
public class FavoriteHanziWordCrossDef {

    @NonNull
    public Long hanziWordId;

    @NonNull
    public Long favoriteId;

    @ColumnInfo(defaultValue = "(datetime('now'))")
    public String datetime;

    public FavoriteHanziWordCrossDef(@NonNull Long hanziWordId, @NonNull Long favoriteId) {
        this.hanziWordId = hanziWordId;
        this.favoriteId = favoriteId;
    }
}
