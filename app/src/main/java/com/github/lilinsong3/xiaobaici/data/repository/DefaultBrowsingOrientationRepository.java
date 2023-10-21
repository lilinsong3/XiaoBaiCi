package com.github.lilinsong3.xiaobaici.data.repository;

import androidx.annotation.OptIn;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.viewpager2.widget.ViewPager2;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DefaultBrowsingOrientationRepository implements BrowsingOrientationRepository {

    private static final String TAG = "DBORepository";

    private final RxDataStore<Preferences> rxDataStore;
    public static final Preferences.Key<Integer> HOME_VIEW_PAGER2_ORIENTATION = PreferencesKeys.intKey("home_view_pager2_orientation");

    @Inject
    public DefaultBrowsingOrientationRepository(RxDataStore<Preferences> dataStore) {
        this.rxDataStore = dataStore;
    }

    @OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
    @Override
    public Flowable<Integer> getBrowsingOrientationStream() {
        return rxDataStore.data()
                .map(preferences -> {
                    Integer orientation = preferences.get(HOME_VIEW_PAGER2_ORIENTATION);
                    return orientation == null ? ViewPager2.ORIENTATION_VERTICAL : orientation;
                });
    }

    @OptIn(markerClass = kotlinx.coroutines.ExperimentalCoroutinesApi.class)
    @Override
    public Completable setBrowsingOrientation(Integer orientation) {
        return rxDataStore.updateDataAsync(preferences -> {
                    MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                    mutablePreferences.set(HOME_VIEW_PAGER2_ORIENTATION, orientation);
                    return Single.just(mutablePreferences);
                })
                .ignoreElement()
                .subscribeOn(Schedulers.io());
    }
}
