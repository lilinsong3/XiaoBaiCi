package com.github.lilinsong3.xiaobaici.util;

import androidx.lifecycle.LifecycleOwner;

import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;

public abstract class RxUtil {
    public static <@NonNull T> AutoDisposeConverter<T> autoDispose(@NonNull LifecycleOwner lifecycleOwner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner));
    }

    public static <@NonNull T> AutoDisposeConverter<T> autoDispose(CompletableSource completableSource) {
        return AutoDispose.autoDisposable(completableSource);
    }

    public static void cleanup(
            @NonNull Completable completable,
            @NonNull LifecycleOwner lifecycleOwner
    ) {
        completable.observeOn(AndroidSchedulers.mainThread())
                .to(RxUtil.autoDispose(lifecycleOwner))
                .subscribe();
    }
}
