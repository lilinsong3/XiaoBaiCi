package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HanziWordModel {

    @NonNull
    public final Long id;

    @NonNull
    public final String subject;

    @NonNull
    public final String pinyin;

    @NonNull
    public final String pinyinAbbr;

    @Nullable
    public final String meaning;

    @Nullable
    public final String usage;

    @Nullable
    public final String example;

    @NonNull
    public final Integer favoriteCount;

    public HanziWordModel(
            @NonNull Long id,
            @NonNull String subject,
            @NonNull String pinyin, @NonNull String pinyinAbbr, @Nullable String meaning,
            @Nullable String usage,
            @Nullable String example,
            @NonNull Integer favoriteCount
    ) {
        this.id = id;
        this.subject = subject;
        this.pinyin = pinyin;
        this.pinyinAbbr = pinyinAbbr;
        this.meaning = meaning;
        this.usage = usage;
        this.example = example;
        this.favoriteCount = favoriteCount;
    }
}
