package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.Nullable;

public class BrowsingHistoryModel {
    public final Long hanziWordId;
    public final String wordSubject;
    public final String datetime;

    public BrowsingHistoryModel(Long hanziWordId, String wordSubject, String datetime) {
        this.hanziWordId = hanziWordId;
        this.wordSubject = wordSubject;
        this.datetime = datetime;
    }

    @Override
    public int hashCode() {
        return hanziWordId.intValue();
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
}
