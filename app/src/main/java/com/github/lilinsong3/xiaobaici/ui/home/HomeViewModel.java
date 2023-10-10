package com.github.lilinsong3.xiaobaici.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.widget.ViewPager2;

import com.github.lilinsong3.xiaobaici.data.repository.HanziWordRepository;
import com.github.lilinsong3.xiaobaici.data.repository.SearchHistoryRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";
    @ViewPager2.OffscreenPageLimit
    public static final Integer OFFSCREEN_PAGE_LIMIT = 3;
    private static final Integer INITIAL_POSITION = -1;
    private final HanziWordRepository hanziWordRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(true);
    public final LiveData<Boolean> showLoading = Transformations.distinctUntilChanged(loading);

    private final MutableLiveData<Integer> viewPosition = new MutableLiveData<>(INITIAL_POSITION);
    private final MediatorLiveData<List<Long>> hanziWordIdData = new MediatorLiveData<>();

    private static final String SEARCH_KEYWORD = "keyword";
    private final PublishSubject<String> debounceSearchInput = PublishSubject.create();
    private final SearchHistoryRepository searchHistoryRepository;
    private final SavedStateHandle savedStateHandle;
//    private final MutableLiveData<String> liveInputKeyword = new MutableLiveData<>("");
//    private final LiveData<String> distinctLiveInputKeyword = Transformations.distinctUntilChanged(liveInputKeyword);
//    public final LiveData<String> submittingKeyword;
//    public final LiveData<List<SearchHistoryModel>> searchHistoryList;
//    public final LiveData<PagingData<SearchSuggestionModel>> searchSuggestions;
//    public final LiveData<PagingData<HanziWordModel>> searchResults;

    @Inject
    public HomeViewModel(
            HanziWordRepository hanziWordRepository,
            SearchHistoryRepository searchHistoryRepository,
            SavedStateHandle savedStateHandle
    ) {
        this.hanziWordRepository = hanziWordRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.savedStateHandle = savedStateHandle;

        LiveData<List<Long>> moreIdData = Transformations.switchMap(viewPosition, currentPosition -> LiveDataReactiveStreams.fromPublisher(loadMoreIds(currentPosition)));

        hanziWordIdData.addSource(moreIdData, this::observeMoreIdData);
    }

    public void setViewPosition(Integer newPosition) {
        viewPosition.setValue(newPosition);
    }

    private Integer computeRequestNumber(Integer position) {
        // 计算 需要请求的数量 = 离屏页面数量限制 - 当前离屏剩余页面数量(= 当前总页面数量 - 当前页面位置 - 1)
        // 比如，0123，currentPages = 4，currentPosition = 2，offscreenPageLimit = 3
        // requestNumber = 3 - (4 - 2 -1) = 2
        return OFFSCREEN_PAGE_LIMIT - ((hanziWordIdData.getValue() == null ? new ArrayList<>() : hanziWordIdData.getValue()).size() - position - 1);
    }

    private Flowable<List<Long>> loadMoreIds(Integer position) {
        Integer requestNumber = computeRequestNumber(position);

        // 为了不频繁地展示loading，只在请求数量大于等于offscreenPageLimit时才展示loading
        if (requestNumber >= OFFSCREEN_PAGE_LIMIT) {
            loading.setValue(true);
        }

        return hanziWordRepository.getHanziWordIdsStream(requestNumber).doFinally(() -> loading.postValue(false));
    }

    private void observeMoreIdData(List<Long> moreIds) {
        List<Long> oldHanziWordIds = hanziWordIdData.getValue() == null ? new ArrayList<>() : hanziWordIdData.getValue();
        List<Long> newHanziWordIds = new ArrayList<>(oldHanziWordIds.size() + moreIds.size());
        newHanziWordIds.addAll(oldHanziWordIds);
        newHanziWordIds.addAll(moreIds);
        hanziWordIdData.setValue(newHanziWordIds);
    }

    public LiveData<List<Long>> getHanziWordIdData() {
        return hanziWordIdData;
    }

    public void setLoading(Boolean isLoading) {
        loading.setValue(isLoading);
    }

    public void refreshPageData() {
        loading.setValue(true);
        hanziWordIdData.setValue(new ArrayList<>());
        viewPosition.setValue(INITIAL_POSITION);
    }

    // 搜索事件
    public void search(String keyword) {
        // 保存历史记录，并触发关键词状态改变
        disposables.add(searchHistoryRepository.saveHistory(keyword).subscribe(() -> savedStateHandle.set(SEARCH_KEYWORD, keyword)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }
}
