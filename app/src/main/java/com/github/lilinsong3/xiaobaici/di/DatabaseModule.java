package com.github.lilinsong3.xiaobaici.di;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.github.lilinsong3.xiaobaici.BuildConfig;
import com.github.lilinsong3.xiaobaici.data.local.AppDatabase;
import com.github.lilinsong3.xiaobaici.data.local.dao.BrowsingHistoryDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.FavoriteDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.FavoriteHanziWordDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.HanziWordDao;
import com.github.lilinsong3.xiaobaici.data.local.dao.SearchHistoryDao;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Singleton
    @Provides
    public static AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        RoomDatabase.Builder<AppDatabase> databaseBuilder = Room
                .databaseBuilder(context, AppDatabase.class, "xiaobaici.db")
                .createFromAsset("database/xiaobaici_v1.db");
        if (BuildConfig.DEBUG) {
            databaseBuilder.setQueryCallback(
                    (sqlQuery, bindArgs) -> Log.d("provideAppDatabase", sqlQuery + " args:" + bindArgs),
                    Executors.newCachedThreadPool()
            );
        }
        return databaseBuilder.build();
    }

    @Provides
    public static HanziWordDao provideHanziWordDao(AppDatabase appDatabase) {
        return appDatabase.hanziWordDao();
    }

    @Provides
    public static SearchHistoryDao provideSearchHistoryDao(AppDatabase appDatabase) {
        return appDatabase.searchHistoryDao();
    }

    @Provides
    public static FavoriteDao provideFavoriteDao(AppDatabase appDatabase) {
        return appDatabase.favoriteDao();
    }

    @Provides
    public static FavoriteHanziWordDao provideFavoriteHanziWordDao(AppDatabase appDatabase) {
        return appDatabase.favoriteHanziWordDao();
    }

    @Provides
    public static BrowsingHistoryDao provideBrowsingHistoryDao(AppDatabase appDatabase) {
        return appDatabase.browsingHistoryDao();
    }

}
