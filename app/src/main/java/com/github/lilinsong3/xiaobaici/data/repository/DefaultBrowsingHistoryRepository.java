package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.annotation.NonNull;
import androidx.paging.PagingSource;

import com.github.lilinsong3.xiaobaici.data.local.dao.BrowsingHistoryDao;
import com.github.lilinsong3.xiaobaici.data.local.entities.BrowsingHistory;
import com.github.lilinsong3.xiaobaici.data.model.BrowsingHistoryModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class DefaultBrowsingHistoryRepository implements BrowsingHistoryRepository {

    private final BrowsingHistoryDao localDataSource;

    @Inject
    public DefaultBrowsingHistoryRepository(BrowsingHistoryDao localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public PagingSource<Integer, BrowsingHistoryModel> getBrowsingHistoryModelPage(@NonNull String startDatetime, @NonNull String endDatetime) {
        return localDataSource.queryPage(startDatetime, endDatetime);
    }

    @Override
    public Completable newBrowsingHistory(Long hanziWordId) {
        return localDataSource.insertRow(hanziWordId).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable removeBrowsingHistory(Long hanziWordId) {
        return localDataSource.deleteRow(new BrowsingHistory().hanziWordId(hanziWordId)).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable clear() {
        return localDataSource.clear().subscribeOn(Schedulers.io());
    }
}
