package com.github.lilinsong3.xiaobaici.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.github.lilinsong3.xiaobaici.data.local.entities.SearchHistory;
import com.github.lilinsong3.xiaobaici.data.model.SearchHistoryModel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface SearchHistoryDao {
    @Query("REPLACE INTO SearchHistory(word) VALUES(:word)")
    Completable insert(String word);
    @Delete(entity = SearchHistory.class)
    Completable delete(SearchHistoryModel model);
    @Delete(entity = SearchHistory.class)
    Completable delete(List<SearchHistoryModel> searchHistoryList);
    @Query("DELETE FROM SearchHistory")
    Completable clear();
    @Query("SELECT * FROM SearchHistory ORDER BY datetime DESC,id DESC")
    Flowable<List<SearchHistoryModel>> queryAll();
}
