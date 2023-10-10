package com.github.lilinsong3.xiaobaici.ui.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.github.lilinsong3.xiaobaici.data.model.FavoriteModel;
import com.github.lilinsong3.xiaobaici.data.repository.FavoriteRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoritesViewModel extends ViewModel {
    public final LiveData<List<FavoriteModel>> favoriteModels;

    @Inject
    public FavoritesViewModel(FavoriteRepository favoriteRepository) {
        favoriteModels = LiveDataReactiveStreams.fromPublisher(favoriteRepository.getFavoriteModelListStream());
    }
}
