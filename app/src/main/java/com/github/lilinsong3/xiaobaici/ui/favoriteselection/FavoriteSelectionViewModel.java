package com.github.lilinsong3.xiaobaici.ui.favoriteselection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.github.lilinsong3.xiaobaici.data.model.FavoriteSelectionModel;
import com.github.lilinsong3.xiaobaici.data.repository.FavoriteHanziWordRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;

@HiltViewModel
public class FavoriteSelectionViewModel extends ViewModel {

    public final LiveData<List<FavoriteSelectionModel>> models;
    public final Long argHanziWordId;

    private final FavoriteHanziWordRepository favoriteHanziWordRepository;

    @Inject
    public FavoriteSelectionViewModel(
            SavedStateHandle savedStateHandle,
            FavoriteHanziWordRepository favoriteHanziWordRepository
    ) {
        argHanziWordId = FavoriteSelectionBottomSheetArgs.fromSavedStateHandle(savedStateHandle).getHanziWordId();
        this.favoriteHanziWordRepository = favoriteHanziWordRepository;
        models = LiveDataReactiveStreams.fromPublisher(this.favoriteHanziWordRepository.getFavoriteSelectionModelListStream(argHanziWordId));
    }

    public Completable handleSelectFavorites(Long[] newSelectedFavoriteIds) {
        return favoriteHanziWordRepository.saveNewFavoriteHanziWords(argHanziWordId, newSelectedFavoriteIds);
    }
}
