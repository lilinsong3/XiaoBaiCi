package com.github.lilinsong3.xiaobaici.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String name = "默认收藏夹";
    public Boolean isDefault = true;

    public Favorite name(String name) {
        this.name = name;
        return this;
    }

    public Favorite isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public Favorite id(Long id) {
        this.id = id;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        //        return "Favorite {id: , " + id + "hanziWordId: " + hanziWordId + "}";
        return "Favorite {id: " + id +
                ", name: " + name +
                ", isDefault: " + isDefault +
                "}";
    }
}
