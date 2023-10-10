package com.github.lilinsong3.xiaobaici.ui.favoriteedit;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.github.lilinsong3.xiaobaici.data.local.entities.Favorite;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteEditModel;
import com.github.lilinsong3.xiaobaici.data.repository.FavoriteRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

@HiltViewModel
public class FavoriteEditViewModel extends ViewModel {

    private static final int NULL_ID = -1;
    private final long argFavoriteId;
    private final FavoriteRepository repository;

    @Inject
    public FavoriteEditViewModel(SavedStateHandle savedStateHandle, FavoriteRepository repository) {
        this.repository = repository;
        argFavoriteId = FavoriteEditFragmentArgs.fromSavedStateHandle(savedStateHandle).getFavoriteId();
    }

    public Boolean isCreateOperation() {
        return argFavoriteId == NULL_ID;
    }

    public Maybe<FavoriteEditModel> loadModel() {
        return repository.getFavoriteEditModel(argFavoriteId);
    }

    public Completable saveData(String name, Boolean isDefault) {
        Favorite favorite = new Favorite().name(name).isDefault(isDefault);
        if (argFavoriteId != NULL_ID) {
            favorite.id = argFavoriteId;
        }
        return repository.save(favorite);
    }
}
