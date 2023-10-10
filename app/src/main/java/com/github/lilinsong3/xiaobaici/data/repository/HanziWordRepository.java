package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.paging.PagingSource;

import com.github.lilinsong3.xiaobaici.data.model.HanziWordModel;
import com.github.lilinsong3.xiaobaici.data.model.SearchSuggestionModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public interface HanziWordRepository {
    Flowable<List<Long>> getHanziWordIdsStream(Integer size);

    Flowable<HanziWordModel> getHanziWordModelStream(Long id);

    PagingSource<Integer, HanziWordModel> searchPagingHanziWordBy(String keywords);

    PagingSource<Integer, SearchSuggestionModel> getPagingSearchSuggestionBy(String keyword);

    Flowable<List<SearchSuggestionModel>> getSearchSuggestionModelsStream(String keyword);
}
