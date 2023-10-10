package com.github.lilinsong3.xiaobaici.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.lilinsong3.xiaobaici.data.local.entities.SearchHistory;

public class SearchHistoryModel {
    public final Long id;
    public final String word;

    public final String datetime;

    public SearchHistoryModel(Long id, String word, String datetime) {
        this.id = id;
        this.word = word;
        this.datetime = datetime;
    }

    public static SearchHistoryModel from(SearchHistory searchHistory) {
        return new SearchHistoryModel(searchHistory.id, searchHistory.word, searchHistory.datetime);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof SearchHistoryModel) {
            SearchHistoryModel searchHistoryModelObj = (SearchHistoryModel) obj;
            return this.word.equals(searchHistoryModelObj.word)
                    && this.datetime.equals(searchHistoryModelObj.datetime);
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "SearchHistoryModel(" + id + ", " + word + ")";
    }
}
