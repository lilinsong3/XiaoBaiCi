package com.github.lilinsong3.xiaobaici.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.lilinsong3.xiaobaici.data.local.entities.Favorite;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteEditModel;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteModel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FavoriteDao {

    @Query("SELECT id FROM Favorite WHERE isDefault = 1")
    Maybe<Long> queryDefaultFavoriteId();

    @Query("SELECT * FROM Favorite WHERE isDefault = 1")
    Maybe<Favorite> queryDefaultFavorite();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Favorite favorite);

    @Query("SELECT F.*, COUNT(FHWCD.hanziWordId) hanziWordCount FROM Favorite F LEFT OUTER JOIN FavoriteHanziWordCrossDef FHWCD ON F.id = FHWCD.favoriteId GROUP BY F.id")
    Flowable<List<FavoriteModel>> queryAllWithWordCount();

    @Query("SELECT F.*, COUNT(FHWCD.hanziWordId) hanziWordCount FROM Favorite F LEFT OUTER JOIN FavoriteHanziWordCrossDef FHWCD ON F.id = FHWCD.favoriteId WHERE F.id = :favoriteId GROUP BY F.id")
    Flowable<FavoriteModel> queryWithWordCount(Long favoriteId);

    @Query("SELECT * FROM Favorite WHERE id = :favoriteId")
    Maybe<FavoriteEditModel> query(Long favoriteId);

    @Query("SELECT * FROM Favorite WHERE id = :favoriteId")
    Maybe<Favorite> queryById(Long favoriteId);

    @Query("DELETE FROM Favorite WHERE id = :id")
    Completable deleteById(Long id);
}
