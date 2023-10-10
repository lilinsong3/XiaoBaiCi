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
//        String keyword = rawKeyword.trim();
//        return localDataSource.queryAll().takeWhile(list -> needChange(list, keyword)).concatMapCompletable(list -> {
//            Completable result = localDataSource.insert(keyword);
//            // 提取需要删除的元素
//            List<SearchHistoryModel> deletingList = list.stream()
//                    .filter(historyItem -> {
//                        int itemIndex = list.indexOf(historyItem);
//                        return (historyItem.word.equals(keyword) && itemIndex > 0) || itemIndex > MAX_ROW_NUMBER - 2;
//                    })
//                    .collect(Collectors.toList());
//            if (!deletingList.isEmpty()) {
//                result = localDataSource.delete(deletingList).andThen(result);
//            }
//            return result;
//        }).subscribeOn(Schedulers.io());
    }

    private Boolean needChange(List<SearchHistoryModel> oldHistories, String searchWord) {
        if (oldHistories.isEmpty()) {
            return true;
        }
//        if (oldHistories.get(0).word.equals(searchWord)) {
//            return false;
//        }
        return oldHistories.stream().anyMatch(oldHistory -> oldHistory.word.equals(searchWord)) ;
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
