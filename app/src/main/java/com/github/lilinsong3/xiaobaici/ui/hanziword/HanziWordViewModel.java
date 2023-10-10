package com.github.lilinsong3.xiaobaici.ui.hanziword;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.github.lilinsong3.xiaobaici.data.model.HanziWordModel;
import com.github.lilinsong3.xiaobaici.data.repository.BrowsingHistoryRepository;
import com.github.lilinsong3.xiaobaici.data.repository.FavoriteHanziWordRepository;
import com.github.lilinsong3.xiaobaici.data.repository.HanziWordRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@HiltViewModel
public class HanziWordViewModel extends ViewModel {
    private final FavoriteHanziWordRepository favoriteHanziWordRepository;
    private final BrowsingHistoryRepository browsingHistoryRepository;

    public final LiveData<HanziWordModel> hanziWordModel;

    private final MediatorLiveData<Boolean> thisFavorite = new MediatorLiveData<>();

    public final MutableLiveData<Boolean> loading = new MutableLiveData<>(true);

    @NonNull
    public final Long argHanziWordId;

    @Inject
    public HanziWordViewModel(
            SavedStateHandle savedStateHandle,
            FavoriteHanziWordRepository favoriteHanziWordRepository,
            BrowsingHistoryRepository browsingHistoryRepository,
            HanziWordRepository hanziWordRepository
    ) {
        this.favoriteHanziWordRepository = favoriteHanziWordRepository;
        this.browsingHistoryRepository = browsingHistoryRepository;
        // 获取id参数
        argHanziWordId = HanziWordFragmentArgs.fromSavedStateHandle(savedStateHandle).getHanziWordId();
        hanziWordModel = LiveDataReactiveStreams.fromPublisher(hanziWordRepository.getHanziWordModelStream(argHanziWordId));
        thisFavorite.addSource(hanziWordModel, model -> thisFavorite.setValue(model.favoriteCount > 0));
    }

    public MediatorLiveData<Boolean> getThisFavorite() {
        return thisFavorite;
    }

    public void setThisFavorite(Boolean ofCourse) {
        thisFavorite.setValue(ofCourse);
    }

    public Completable addBrowsingHistory() {
        return browsingHistoryRepository.newBrowsingHistory(argHanziWordId);
    }

    public Single<long[]> loadFavoriteIds() {
        return favoriteHanziWordRepository.getFavoriteIdList(argHanziWordId);
    }

    public Completable addThisToDefault() {
        return favoriteHanziWordRepository.addToDefaultFavorite(argHanziWordId);
    }

    public Completable cancelFavorite() {
        return favoriteHanziWordRepository.saveNewFavoriteHanziWords(argHanziWordId, new Long[0]);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void setLoading(Boolean isLoading) {
        this.loading.setValue(isLoading);
    }
}
