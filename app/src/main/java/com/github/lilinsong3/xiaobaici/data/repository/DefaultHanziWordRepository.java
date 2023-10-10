package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;

import com.github.lilinsong3.xiaobaici.data.local.dao.HanziWordDao;
import com.github.lilinsong3.xiaobaici.data.model.HanziWordModel;
import com.github.lilinsong3.xiaobaici.data.model.SearchSuggestionModel;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class DefaultHanziWordRepository implements HanziWordRepository {

    private static final String TAG = "DefaultHanziWordRepository";

    private final HanziWordDao localDataSource;

    @Nullable
    private static Long idStart;

    @Nullable
    private static Long idBound;

    @Inject
    public DefaultHanziWordRepository(HanziWordDao localDataSource) {
        this.localDataSource = localDataSource;
    }

    @NonNull
    private Single<Long> getIdStart() {
        if (idStart == null) {
            return localDataSource.queryMinId().doOnSuccess(id -> idStart = id);
        }
        return Single.just(idStart);
    }

    @NonNull
    private Single<Long> getIdBound() {
        if (idBound == null) {
            return localDataSource.queryMaxId().doOnSuccess(id -> idBound = id + 1);
        }
        return Single.just(idBound);
    }

    @Override
    public Flowable<List<Long>> getHanziWordIdsStream(Integer size) {
        if (size < 1) {
            return Flowable.empty();
        }
        return Single.zip(
                        getIdStart(),
                        getIdBound(),
                        // 在[max(rowid), max(rowid)]区间，随即生成id
                        (origin, bound) -> ThreadLocalRandom.current().nextLong(origin, bound)
                )
                // 重复生成
                .repeat(size)
                // 收集
                .toList()
                // 查询
                .concatMap(localDataSource::queryIdsIn)
                .toFlowable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<HanziWordModel> getHanziWordModelStream(Long id) {
        return localDataSource.queryHanziWordModelById(id);
    }

    @Override
    public PagingSource<Integer, HanziWordModel> searchPagingHanziWordBy(String keywords) {
        return localDataSource.ftsPagingModelBy(keywords);
    }

    @Override
    public Flowable<List<SearchSuggestionModel>> getSearchSuggestionModelsStream(String keyword) {
        return localDataSource.querySuggestionsBy(keyword);
    }

    @Override
    public PagingSource<Integer, SearchSuggestionModel> getPagingSearchSuggestionBy(String keyword) {
        return localDataSource.queryPagingSuggestionModelBy(keyword);
    }
}
