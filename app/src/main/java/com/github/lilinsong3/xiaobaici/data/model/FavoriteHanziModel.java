package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.NonNull;

public class FavoriteHanziModel {
    @NonNull
    public final Long hanziWordId;

    @NonNull
    public final String subject;

    @NonNull
    public final String datetime;

    public FavoriteHanziModel(@NonNull Long hanziWordId, @NonNull String subject, @NonNull String datetime) {
        this.hanziWordId = hanziWordId;
        this.subject = subject;
        this.datetime = datetime;
    }
}
