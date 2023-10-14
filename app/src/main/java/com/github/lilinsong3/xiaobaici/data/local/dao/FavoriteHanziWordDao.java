package com.github.lilinsong3.xiaobaici.data.local.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.github.lilinsong3.xiaobaici.data.local.entities.FavoriteHanziWordCrossDef;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteHanziModel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FavoriteHanziWordDao {

    @Query("SELECT fhw.hanziWordId, hw.subject, fhw.datetime FROM FavoriteHanziWordCrossDef fhw INNER JOIN HanziWord hw ON hw.rowid = fhw.hanziWordId WHERE fhw.favoriteId = :favoriteId")
    PagingSource<Integer, FavoriteHanziModel> queryPagingFavoriteHanziByFavoriteId(Long favoriteId);

    @Query("DELETE FROM FavoriteHanziWordCrossDef WHERE hanziWordId = :hanziWordId AND favoriteId NOT IN (:favoriteIds)")
    Completable deleteRowsIfUnmatchable(Long[] favoriteIds, Long hanziWordId);

    @Query("INSERT OR IGNORE INTO FavoriteHanziWordCrossDef(hanziWordId, favoriteId) VALUES (:hanziWordId, :favoriteId)")
    Completable insert(Long hanziWordId, Long favoriteId);

    @Query("DELETE FROM FavoriteHanziWordCrossDef WHERE favoriteId = :favoriteId")
    Completable clearByFavoriteId(Long favoriteId);

    @Delete
    Completable delete(List<FavoriteHanziWordCrossDef> crossDefList);

    @Query("SELECT favoriteId FROM FavoriteHanziWordCrossDef WHERE hanziWordId = :hanziWordId")
    Flowable<List<Long>> queryFavoriteIdsByHanziWordId(Long hanziWordId);

    @Query("SELECT favoriteId FROM FavoriteHanziWordCrossDef WHERE hanziWordId = :hanziWordId")
    Single<List<Long>> queryFIdsByHanziWordId(Long hanziWordId);
}
