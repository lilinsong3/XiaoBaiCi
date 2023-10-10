package com.github.lilinsong3.xiaobaici.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.github.lilinsong3.xiaobaici.data.model.HanziWordModel;
import com.github.lilinsong3.xiaobaici.data.model.SearchHistoryModel;
import com.github.lilinsong3.xiaobaici.data.model.SearchSuggestionModel;
import com.github.lilinsong3.xiaobaici.data.repository.HanziWordRepository;
import com.github.lilinsong3.xiaobaici.data.repository.SearchHistoryRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

@HiltViewModel
public class SearchViewModel extends ViewModel {
    private static final String TAG = "HanziWordListViewModel";
    private static final String SEARCH_KEYWORD = "keyword";
    private final PublishSubject<String> debounceSearchInput = PublishSubject.create();
    private final HanziWordRepository hanziWordRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final CompositeDisposable disposables;
    private final SavedStateHandle savedStateHandle;
    private final MutableLiveData<String> inputtingKeyword = new MutableLiveData<>("");
    public final LiveData<String> submitKeyword;
    public final LiveData<List<SearchHistoryModel>> searchHistoryList;
    public final LiveData<List<SearchSuggestionModel>> searchSuggestions;
    public final LiveData<PagingData<HanziWordModel>> searchResults;

    @Inject
    public SearchViewModel(HanziWordRepository hanziWordRepository, SearchHistoryRepository searchHistoryRepository, SavedStateHandle savedStateHandle) {
        this.hanziWordRepository = hanziWordRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.savedStateHandle = savedStateHandle;
        disposables = new CompositeDisposable();
        submitKeyword = savedStateHandle.getLiveData(SEARCH_KEYWORD);
        // 提交的搜索词改变 -> 搜索历史列表改变
        searchHistoryList = LiveDataReactiveStreams.fromPublisher(searchHistoryRepository.getSearchHistoryModelStream());
        // 实时输入的关键词改变 -> 搜索建议改变
        searchSuggestions = Transformations.switchMap(inputtingKeyword, keyword ->
                LiveDataReactiveStreams.fromPublisher(hanziWordRepository.getSearchSuggestionModelsStream(keyword))
        );
        // 提交的搜索词改变 -> 搜索结果改变
        searchResults = loadSearchResultBySavedKeyword(submitKeyword);
        // 监听实时输入的关键词，以及去抖动
        setupDebounceSearchInput();
    }

    public Completable deleteHistory(SearchHistoryModel model) {
        return searchHistoryRepository.deleteHistory(model);
    }

    // 监听搜索输入框，去抖动
    private void setupDebounceSearchInput() {
        disposables.add(debounceSearchInput
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .subscribe(inputtingKeyword::postValue)
        );
    }

    private LiveData<PagingData<HanziWordModel>> loadSearchResultBySavedKeyword(LiveData<String> keywordData) {
        LiveData<PagingData<HanziWordModel>> result = Transformations.switchMap(
                keywordData,
                keyword -> PagingLiveData.getLiveData(new Pager<>(
                        new PagingConfig(
                                8,
                                8,
                                true,
                                15
                        ),
                        () -> hanziWordRepository.searchPagingHanziWordBy(keyword)
                ))
        );
        // return cached result
        return PagingLiveData.cachedIn(result, ViewModelKt.getViewModelScope(this));
    }

    // 发射搜索关键词
    public void onKeywordInput(String keyword) {
        debounceSearchInput.onNext(keyword);
    }

    public LiveData<String> getInputtingKeyword() {
        return inputtingKeyword;
    }

    public void search(String keyword) {
        // 保存历史记录，并触发关键词状态改变
        if (!keyword.trim().isEmpty()) {
            disposables.add(
                    searchHistoryRepository.saveHistory(keyword)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> savedStateHandle.set(SEARCH_KEYWORD, keyword))
            );
        }
    }

    public Completable clearHistories() {
        return searchHistoryRepository.clearAllHistories();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }
}