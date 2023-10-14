package com.github.lilinsong3.xiaobaici.data.local.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.github.lilinsong3.xiaobaici.data.model.HanziWordModel;
import com.github.lilinsong3.xiaobaici.data.model.SearchSuggestionModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface HanziWordDao {

    @Query("SELECT min(rowid) FROM HanziWord")
    Single<Long> queryMinId();

    @Query("SELECT max(rowid) FROM HanziWord")
    Single<Long> queryMaxId();

    @Query("SELECT rowid FROM HanziWord WHERE rowid IN (:ids)")
    Single<List<Long>> queryIdsIn(List<Long> ids);

    @Query("SELECT a.rowid id, a.*, count(favoriteId) favoriteCount  FROM HanziWord a LEFT OUTER JOIN FavoriteHanziWordCrossDef f ON a.rowid = f.hanziWordId WHERE a.rowid = :id")
    Flowable<HanziWordModel> queryHanziWordModelById(Long id);

//    @Query("SELECT a.rowid AS id, a.*, f.rowid AS favoriteId FROM HanziWord a LEFT JOIN favorite f ON a.rowid = f.hanziWordId WHERE HanziWord MATCH :keyword")
    // 在子查询中使用matchinfo按匹配度排序
    @Query("SELECT h.rowid id, h.*, count(f.favoriteId) favoriteCount FROM HanziWord h JOIN (\n" +
            "SELECT rowid, matchinfo(HanziWord, 'y') hits, matchinfo(HanziWord, 's') longest_hit FROM HanziWord\n" +
            " WHERE subject MATCH :keyword \n" +
            "ORDER BY quote(hits) DESC, quote(longest_hit) DESC /* LIMIT 20*/\n" +
            ") rankable \n" +
            "ON h.rowid = rankable.rowid \n" +
            "LEFT OUTER JOIN FavoriteHanziWordCrossDef f ON h.rowid = f.hanziWordId GROUP BY h.rowid \n" +
            "ORDER BY quote(hits) DESC, quote(longest_hit) DESC")
    PagingSource<Integer, HanziWordModel> ftsPagingModelBy(String keyword);

    @Query("SELECT rowid id, subject suggestion FROM HanziWord h JOIN (\n" +
            "SELECT rowid, matchinfo(HanziWord, 'y') hits, matchinfo(HanziWord, 's') longest_hit FROM HanziWord WHERE subject MATCH :keyword \n" +
            "ORDER BY quote(hits) DESC, quote(longest_hit) DESC  LIMIT 10\n" +
            ") rankable \n" +
            "ON h.rowid = rankable.rowid ORDER BY quote(hits) DESC, quote(longest_hit) DESC")
    Flowable<List<SearchSuggestionModel>> querySuggestionsBy(String keyword);
}
