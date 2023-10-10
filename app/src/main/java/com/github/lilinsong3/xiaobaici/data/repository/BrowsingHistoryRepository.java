package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.annotation.NonNull;
import androidx.paging.PagingSource;

import com.github.lilinsong3.xiaobaici.data.model.BrowsingHistoryModel;

import io.reactivex.rxjava3.core.Completable;

public interface BrowsingHistoryRepository {
    PagingSource<Integer, BrowsingHistoryModel> getBrowsingHistoryModelPage(@NonNull String startDatetime, @NonNull String endDatetime);
    Completable newBrowsingHistory(Long hanziWordId);
    Completable removeBrowsingHistory(Long hanziWordId);
    Completable clear();
}
