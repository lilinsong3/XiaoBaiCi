package com.github.lilinsong3.xiaobaici.data.repository;

import com.github.lilinsong3.xiaobaici.data.model.SearchHistoryModel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface SearchHistoryRepository {
    Flowable<List<SearchHistoryModel>> getSearchHistoryModelStream();

    Completable saveHistory(String rawKeyword);

    Completable deleteHistory(SearchHistoryModel model);

    Completable clearAllHistories();
}
