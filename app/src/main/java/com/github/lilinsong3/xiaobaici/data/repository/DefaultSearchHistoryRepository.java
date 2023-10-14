package com.github.lilinsong3.xiaobaici.data.repository;

import com.github.lilinsong3.xiaobaici.data.local.dao.SearchHistoryDao;
import com.github.lilinsong3.xiaobaici.data.model.SearchHistoryModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class DefaultSearchHistoryRepository implements SearchHistoryRepository {

    private static final String TAG = "DSearchHistoryRepository";
    private final SearchHistoryDao localDataSource;

    @Inject
    public DefaultSearchHistoryRepository(SearchHistoryDao dataSource) {
        this.localDataSource = dataSource;
    }

    @Override
    public Flowable<List<SearchHistoryModel>> getSearchHistoryModelStream() {
        return localDataSource.queryAll();
    }

    @Override
    public Completable saveHistory(String rawKeyword) {
        return localDataSource.insert(rawKeyword.trim()).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable deleteHistory(SearchHistoryModel model) {
        return localDataSource.delete(model).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable clearAllHistories() {
        return localDataSource.clear().subscribeOn(Schedulers.io());
    }
}
