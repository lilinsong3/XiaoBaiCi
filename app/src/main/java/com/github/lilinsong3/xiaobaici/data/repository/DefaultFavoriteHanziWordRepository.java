package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.annotation.NonNull;
import androidx.paging.PagingSource;

import com.github.lilinsong3.xiaobaici.data.local.dao.FavoriteHanziWordDao;
import com.github.lilinsong3.xiaobaici.data.local.entities.FavoriteHanziWordCrossDef;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteHanziModel;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteSelectionModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class DefaultFavoriteHanziWordRepository implements FavoriteHanziWordRepository {

    private static final String TAG = "DefaultFavoriteHanziWordRepository";

    private final FavoriteHanziWordDao localDataSource;

    private final FavoriteRepository favoriteRepository;

    @Inject
    public DefaultFavoriteHanziWordRepository(FavoriteHanziWordDao localDataSource, FavoriteRepository favoriteRepository) {
        this.localDataSource = localDataSource;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Completable saveNewFavoriteHanziWords(@NonNull Long hanziWordId, @NonNull Long[] favoriteIds) {
        Completable[] todos = Arrays.stream(favoriteIds)
                .map(favoriteId -> localDataSource.insert(hanziWordId, favoriteId))
                .toArray(Completable[]::new);
        return localDataSource.deleteRowsIfUnmatchable(favoriteIds, hanziWordId)
                .andThen(Completable.concatArray(todos))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable addToDefaultFavorite(Long hanziWordId) {
        return favoriteRepository.getDefaultFavoriteId()
                .concatMapCompletable(favoriteId -> localDataSource.insert(hanziWordId, favoriteId))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public PagingSource<Integer, FavoriteHanziModel> getPagingFavoriteHanziByFavoriteId(Long id) {
        return localDataSource.queryPagingFavoriteHanziByFavoriteId(id);
    }

    @Override
    public Flowable<List<FavoriteSelectionModel>> getFavoriteSelectionModelListStream(Long hanziWordId) {
        return Flowable.zip(
                favoriteRepository.getFavoriteModelListStream(),
                localDataSource.queryFavoriteIdsByHanziWordId(hanziWordId),
                (favoriteModels, selectedIds) -> favoriteModels.stream()
                        .map(favoriteModel -> new FavoriteSelectionModel(
                                favoriteModel,
                                selectedIds.contains(favoriteModel.id)
                        ))
                        .collect(Collectors.toList())
        ).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<long[]> getFavoriteIdList(Long hanziWordId) {
        return localDataSource.queryFIdsByHanziWordId(hanziWordId)
//                .first(new ArrayList<>())
                .map(ids -> ids.stream().mapToLong(Long::longValue).toArray())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable removeHanziWordsFromFavorite(List<Long> hanziWordIds, Long favoriteId) {
        return localDataSource.delete(
                        hanziWordIds.stream()
                                .map(hanziWordId -> new FavoriteHanziWordCrossDef(
                                        hanziWordId,
                                        favoriteId
                                ))
                                .collect(Collectors.toList())
                )
                .subscribeOn(Schedulers.io());
    }

}
