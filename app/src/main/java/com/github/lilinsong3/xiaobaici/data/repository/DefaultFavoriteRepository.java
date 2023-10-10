package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.annotation.NonNull;

import com.github.lilinsong3.xiaobaici.data.local.dao.FavoriteDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.FavoriteHanziWordDao;
import com.github.lilinsong3.xiaobaici.data.local.entities.Favorite;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteEditModel;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class DefaultFavoriteRepository implements FavoriteRepository {

    private final FavoriteDao localDataSource;
    private final FavoriteHanziWordDao favoriteHanziWordLocalDataSource;

    @Inject
    public DefaultFavoriteRepository(FavoriteDao localDataSource, FavoriteHanziWordDao favoriteHanziWordLocalDataSource) {
        this.localDataSource = localDataSource;
        this.favoriteHanziWordLocalDataSource = favoriteHanziWordLocalDataSource;
    }

    @Override
    public Single<Long> getDefaultFavoriteId() {
        return localDataSource.queryDefaultFavoriteId()
                .switchIfEmpty(newDefaultFavorite())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Long> newDefaultFavorite() {
        return localDataSource.insert(new Favorite()).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable save(@NonNull Favorite favorite) {
        return localDataSource.insert(favorite).ignoreElement().subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<List<FavoriteModel>> getFavoriteModelListStream() {
        return localDataSource.queryAllWithWordCount()
                .concatMapSingle(favoriteModels -> {
                    if (favoriteModels.isEmpty()) {
                        // 如果没有收藏夹，则先创建一个默认收藏夹，并添加到列表，再返回
                        return newDefaultFavorite().map(defaultFavoriteId -> {
                            favoriteModels.add(FavoriteModel.from(new Favorite().id(defaultFavoriteId)));
                            return favoriteModels;
                        });
                    }
                    return Single.just(favoriteModels);
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<FavoriteModel> getFavoriteModelStreamById(@NonNull Long favoriteId) {
        return localDataSource.queryWithWordCount(favoriteId);
    }

    @Override
    public Completable deleteFavoriteById(Long id) {
        return favoriteHanziWordLocalDataSource.clearByFavoriteId(id)
                .andThen(localDataSource.deleteById(id)).subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<FavoriteEditModel> getFavoriteEditModel(Long favoriteId) {
        return localDataSource.query(favoriteId).subscribeOn(Schedulers.io());
    }
}
