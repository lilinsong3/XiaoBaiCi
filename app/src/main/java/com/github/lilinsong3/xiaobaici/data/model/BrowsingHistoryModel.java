package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.lilinsong3.xiaobaici.common.Differentiable;

public class BrowsingHistoryModel implements Differentiable {
    public final Long hanziWordId;
    public final String wordSubject;
    public final String datetime;

    public BrowsingHistoryModel(Long hanziWordId, String wordSubject, String datetime) {
        this.hanziWordId = hanziWordId;
        this.wordSubject = wordSubject;
        this.datetime = datetime;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof BrowsingHistoryModel) {
            BrowsingHistoryModel model = (BrowsingHistoryModel) obj;
            return wordSubject.equals(model.wordSubject)
                    && datetime.equals(model.datetime);
        }
        return false;
    }

    @NonNull
    @Override
    public String getKey() {
        return hanziWordId.toString();
    }
}
