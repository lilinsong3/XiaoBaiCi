package com.github.lilinsong3.xiaobaici.di;

import com.github.lilinsong3.xiaobaici.data.repository.BrowsingHistoryRepository;
import com.github.lilinsong3.xiaobaici.data.repository.DefaultBrowsingHistoryRepository;
import com.github.lilinsong3.xiaobaici.data.repository.DefaultFavoriteHanziWordRepository;
import com.github.lilinsong3.xiaobaici.data.repository.DefaultFavoriteRepository;
import com.github.lilinsong3.xiaobaici.data.repository.DefaultHanziWordRepository;
import com.github.lilinsong3.xiaobaici.data.repository.DefaultSearchHistoryRepository;
import com.github.lilinsong3.xiaobaici.data.repository.FavoriteHanziWordRepository;
import com.github.lilinsong3.xiaobaici.data.repository.FavoriteRepository;
import com.github.lilinsong3.xiaobaici.data.repository.HanziWordRepository;
import com.github.lilinsong3.xiaobaici.data.repository.SearchHistoryRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Singleton
    @Binds
    public abstract HanziWordRepository bindHanziWordRepository(DefaultHanziWordRepository defaultHanziWordRepository);

    @Singleton
    @Binds
    public abstract SearchHistoryRepository bindSearchHistoryRepository(DefaultSearchHistoryRepository defaultSearchHistoryRepository);

    @Singleton
    @Binds
    public abstract FavoriteHanziWordRepository bindFavoriteHanziWordRepository(DefaultFavoriteHanziWordRepository defaultFavoriteHanziWordRepository);

    @Singleton
    @Binds
    public abstract FavoriteRepository bindFavoriteRepository(DefaultFavoriteRepository defaultFavoriteRepository);

    @Singleton
    @Binds
    public abstract BrowsingHistoryRepository bindBrowsingHistoryRepository(DefaultBrowsingHistoryRepository defaultBrowsingHistoryRepository);
}
