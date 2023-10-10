package com.github.lilinsong3.xiaobaici.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = { @Index(value = {"word"}, unique = true)})
public class SearchHistory {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public String word;

    @ColumnInfo(defaultValue = "(datetime('now'))")
    public String datetime;

    public SearchHistory buildWith(Long id) {
        this.id = id;
        return this;
    }
    public SearchHistory buildWith(String word) {
        this.word = word;
        return this;
    }
}
