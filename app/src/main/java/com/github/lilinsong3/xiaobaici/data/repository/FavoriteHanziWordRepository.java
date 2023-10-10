package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.annotation.NonNull;
import androidx.paging.PagingSource;

import com.github.lilinsong3.xiaobaici.data.model.FavoriteHanziModel;
import com.github.lilinsong3.xiaobaici.data.model.FavoriteSelectionModel;
import com.github.lilinsong3.xiaobaici.data.model.WordSubjectModel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface FavoriteHanziWordRepository {

    /**
     * 根据收藏夹id,获取分页的在该收藏夹下的词语
     *
     * @param id 收藏夹id
     * @return 分页的某个收藏夹下的词语
     */
    PagingSource<Integer, WordSubjectModel> getPagingFavoriteWordSubjectByFavoriteId(Long id);

    /**
     * 把词语保存到收藏夹<br>
     * 收藏此词语(@code hanziWordId)的旧收藏夹如果不在新的收藏夹列表(@code favoriteIds)里，那么这些所有的旧收藏夹与该词语的收藏关系将被删除
     * @param hanziWordId 要收藏的词语的id
     * @param favoriteIds 收藏词语的收藏夹id列表
     * @return 成功或失败
     */
    Completable saveNewFavoriteHanziWords(@NonNull Long hanziWordId, @NonNull Long[] favoriteIds);

    /**
     * 添加到默认收藏夹
     * @param hanziWordId 要收藏的词语id
     * @return done OR error
     */
    Completable addToDefaultFavorite(Long hanziWordId);

    /**
     * 取消所有收藏
     * @param hanziWordId 要取消收藏的词语id
     * @return done OR error
     */
    Completable removeHanziWordFromAllFavorites(Long hanziWordId);

    /**
     * 根据收藏夹id,获取分页的在该收藏夹下的词语
     *
     * @param id 收藏夹id
     * @return 分页的某个收藏夹下的词语
     */
    PagingSource<Integer, FavoriteHanziModel> getPagingFavoriteHanziByFavoriteId(Long id);

    /**
     * 清除一个收藏夹下所有的词语
     * @param favoriteId 收藏夹id
     * @return done OR error
     */
    Completable clearAllWordsByFavoriteId(Long favoriteId);

    /**
     * 根据词语id，获取所有收藏了该词语的所有收藏夹id
     * @param hanziWordId 词语id
     * @return 收藏夹id的可观察流
     */
    Flowable<List<Long>> getSelectedFavoriteIdListStream(Long hanziWordId);

    /**
     * 根据词语id，获取所有用于选择收藏夹的FavoriteSelectionModel
     * @param hanziWordId 词语id
     * @return FavoriteSelectionModel列表的可观察流
     */
    Flowable<List<FavoriteSelectionModel>> getFavoriteSelectionModelListStream(Long hanziWordId);

    /**
     * 根据词语id，获取所有收藏了该词语的所有收藏夹id
     * @param hanziWordId 词语id
     * @return 异步结果
     */
    Single<long[]> getFavoriteIdList(Long hanziWordId);

    Completable removeHanziWordsFromFavorite(List<Long> hanziWordIds, Long favoriteId);
}
