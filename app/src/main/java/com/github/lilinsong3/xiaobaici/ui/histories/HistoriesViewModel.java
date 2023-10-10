package com.github.lilinsong3.xiaobaici.ui.histories;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.github.lilinsong3.xiaobaici.data.model.BrowsingHistoryModel;
import com.github.lilinsong3.xiaobaici.data.repository.BrowsingHistoryRepository;
import com.github.lilinsong3.xiaobaici.util.TimeUtil;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;

@HiltViewModel
public class HistoriesViewModel extends ViewModel {

    private static final String TAG = "HistoriesViewModel";

    private final BrowsingHistoryRepository repository;
    public final LiveData<PagingData<BrowsingHistoryModel>> browsingHistoryModelPage;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(true);
    public final LiveData<Boolean> showLoading = Transformations.distinctUntilChanged(loading);

    private final MutableLiveData<Pair<String, String>> datetimeRange = new MutableLiveData<>(Pair.create(TimeUtil.EPOCH, TimeUtil.nowDatetime()));

    @Inject
    public HistoriesViewModel(BrowsingHistoryRepository repository) {
        this.repository = repository;

        browsingHistoryModelPage = Transformations.switchMap(
                datetimeRange,
                datetimeRange -> PagingLiveData.cachedIn(
                        PagingLiveData.getLiveData(
                                new Pager<>(
                                        new PagingConfig(10, 10, true, 15),
                                        () -> this.repository.getBrowsingHistoryModelPage(datetimeRange.first, datetimeRange.second)
                                )
                        ) ,
                        ViewModelKt.getViewModelScope(this)
                )
        );
    }

    public void selectDatetimeRange(Pair<String, String> range) {
        loading.setValue(true);
        datetimeRange.setValue(range);
    }

    public Completable clear() {
        return repository.clear()
                .doOnSubscribe(d -> loading.postValue(true))
                .doFinally(() -> loading.postValue(false));
    }

    public Completable delete(Long hanziWordId) {
        return repository.removeBrowsingHistory(hanziWordId)
                .doOnSubscribe(d -> loading.postValue(true))
                .doFinally(() -> loading.postValue(false));
    }
    public void setLoading(Boolean pageLoading) {
        loading.setValue(pageLoading);
    }
}
