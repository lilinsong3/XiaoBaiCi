package com.github.lilinsong3.xiaobaici.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BrowsingHistory {
    @PrimaryKey
    public Long hanziWordId;

    @ColumnInfo(defaultValue = "(datetime('now'))")
    public String datetime;

    public BrowsingHistory hanziWordId(@NonNull Long hanziWordId) {
        this.hanziWordId = hanziWordId;
        return this;
    }
}
