package com.github.lilinsong3.xiaobaici.util;

import android.content.Context;

import androidx.annotation.OptIn;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

// TODO: 2023/10/20 delete this file
public abstract class DataStoreUtil {
    private static final String DATA_STORE_DEFAULT = "app";
    private static volatile RxDataStore<Preferences> defaultRxDataStore;
//    private static final ArrayMap<String, RxDataStore<Preferences>> RX_DATA_STORE_MAP = new ArrayMap<>(1);
    public static final Preferences.Key<Integer> HOME_VIEW_PAGER2_ORIENTATION = PreferencesKeys.intKey("home_view_pager2_orientation");


    /**
     * 获取默认RxDataStore
     * @param context 创建RxDataStore需要的参数
     * @return 默认RxDataStore
     */
//    public static RxDataStore<Preferences> getRxDataStore(Context context) {
//        return getRxDataStore(context, DATA_STORE_CONFIG);
//    }

//    public static RxDataStore<Preferences> getRxDataStore(Context context, String dataStoreName) {
//        if (RX_DATA_STORE_MAP.get(dataStoreName) == null) {
//            RX_DATA_STORE_MAP.put(dataStoreName, new RxPreferenceDataStoreBuilder(context, dataStoreName).build());
//        }
//        return RX_DATA_STORE_MAP.get(dataStoreName);
//    }


    public static RxDataStore<Preferences> getRxDataStore(Context context) {
        if (defaultRxDataStore == null) {
            synchronized (RxDataStore.class) {
                if (defaultRxDataStore == null) {
                    defaultRxDataStore = new RxPreferenceDataStoreBuilder(context, DATA_STORE_DEFAULT).build();
                }
            }
        }
        return defaultRxDataStore;
    }

    @OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
    public static <K> Maybe<K> getOnce(Context context, Preferences.Key<K> key) {
        return get(context, key).firstElement();
    }

    @OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
    public static <K> Flowable<K> get(Context context, Preferences.Key<K> key) {
        return getRxDataStore(context).data().map(preferences -> preferences.get(key));
    }

//    public static <K> Maybe<K> get(Context context, Preferences.Key<K> key) {
//        return get(context, DATA_STORE_CONFIG, key);
//    }

    @OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
    public static <K> Completable ignoreSet(Context context, Preferences.Key<K> key, K newValue) {
        return set(context, key, newValue).ignoreElement();
    }

    @OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
    public static <K> Single<Preferences> set(Context context, Preferences.Key<K> key, K newValue) {
        return getRxDataStore(context).updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(key, newValue);
            return Single.just(mutablePreferences);
        });
    }

//    public static <K> Single<Preferences> set(Context context, Preferences.Key<K> key, K newValue) {
//        return set(context, DATA_STORE_CONFIG, key, newValue);
//    }

}
