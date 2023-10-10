package com.github.lilinsong3.xiaobaici.data.local.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.github.lilinsong3.xiaobaici.data.local.entities.BrowsingHistory;
import com.github.lilinsong3.xiaobaici.data.model.BrowsingHistoryModel;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface BrowsingHistoryDao {
    @Query("SELECT bh.*, hw.subject wordSubject FROM BrowsingHistory bh JOIN HanziWord hw ON bh.hanziWordId = hw.rowid WHERE :startDatetime <= bh.datetime AND bh.datetime <= :endDatetime ORDER BY bh.datetime DESC")
    PagingSource<Integer, BrowsingHistoryModel> queryPage(String startDatetime, String endDatetime);

    @Query("REPLACE INTO BrowsingHistory(hanziWordId) VALUES (:hanziWordId)")
    Completable insertRow(Long hanziWordId);

    @Delete
    Completable deleteRow(BrowsingHistory browsingHistory);

    @Query("DELETE FROM BrowsingHistory")
    Completable clear();
}
