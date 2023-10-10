package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.annotation.NonNull;

import com.github.lilinsong3.xiaobaici.data.local.entities.Favorite;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteEditModel;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteModel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface FavoriteRepository {

    Single<Long> getDefaultFavoriteId();

    Single<Long> newDefaultFavorite();

    Completable save(@NonNull Favorite favorite);

    Flowable<List<FavoriteModel>> getFavoriteModelListStream();

    Flowable<FavoriteModel> getFavoriteModelStreamById(@NonNull Long favoriteId);

    Completable deleteFavoriteById(Long id);

    Maybe<FavoriteEditModel> getFavoriteEditModel(Long favoriteId);
}
