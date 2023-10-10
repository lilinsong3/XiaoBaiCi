package com.github.lilinsong3.xiaobaici.ui.favorite;

import androidx.annotation.NonNull;
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

import com.github.lilinsong3.xiaobaici.data.model.FavoriteHanziModel;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteModel;
import com.github.lilinsong3.xiaobaici.data.repository.FavoriteHanziWordRepository;
import com.github.lilinsong3.xiaobaici.data.repository.FavoriteRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;

@HiltViewModel
public class FavoriteViewModel extends ViewModel {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteHanziWordRepository favoriteHanziWordRepository;

    public final LiveData<FavoriteModel> favoriteModel;
    public final LiveData<PagingData<FavoriteHanziModel>> pagingFavoriteHanziModels;

    private final MutableLiveData<Boolean> batchMode = new MutableLiveData<>(false);
    public final LiveData<Boolean> distinctBatchMode = Transformations.distinctUntilChanged(batchMode);

    private final MutableLiveData<Boolean> pageLoading = new MutableLiveData<>(true);
    public final LiveData<Boolean> loading = Transformations.distinctUntilChanged(pageLoading);

    @NonNull
    public final Long argFavoriteId;

    @Inject
    public FavoriteViewModel(
            SavedStateHandle savedStateHandle,
            FavoriteRepository favoriteRepository,
            FavoriteHanziWordRepository favoriteHanziWordRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteHanziWordRepository = favoriteHanziWordRepository;

        argFavoriteId = FavoriteFragmentArgs.fromSavedStateHandle(savedStateHandle).getFavoriteId();
        this.favoriteModel = LiveDataReactiveStreams.fromPublisher(this.favoriteRepository.getFavoriteModelStreamById(argFavoriteId));
        this.pagingFavoriteHanziModels = PagingLiveData.cachedIn(
                PagingLiveData.getLiveData(new Pager<>(
                        new PagingConfig(10),
                        () -> favoriteHanziWordRepository.getPagingFavoriteHanziByFavoriteId(argFavoriteId)
                )),
                ViewModelKt.getViewModelScope(this)
        );
    }

    public void setBatchMode(Boolean enterBatchMode) {
        batchMode.setValue(enterBatchMode);
    }

    public Completable doDelete() {
        return favoriteRepository.deleteFavoriteById(argFavoriteId)
                .doFinally(() -> pageLoading.postValue(false));
    }

    public Completable cancelFavorite(Long hanziWordId) {
        return cancelFavorite(Collections.singletonList(hanziWordId));
    }

    public Completable cancelFavorite(List<Long> selectedFavoriteIds) {
        return favoriteHanziWordRepository.removeHanziWordsFromFavorite(selectedFavoriteIds, argFavoriteId)
                .doFinally(() -> pageLoading.postValue(false));
    }

    public void setPageLoading(Boolean loading) {
        pageLoading.setValue(loading);
    }
}