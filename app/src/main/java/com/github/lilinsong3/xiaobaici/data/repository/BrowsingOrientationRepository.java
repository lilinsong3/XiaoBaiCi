package com.github.lilinsong3.xiaobaici.data.repository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface BrowsingOrientationRepository {
    Flowable<Integer> getBrowsingOrientationStream();
    Completable setBrowsingOrientation(Integer orientation);
}
