package com.github.lilinsong3.xiaobaici.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public class LifecycleLogger implements DefaultLifecycleObserver {

    private static final String TAG = "LifecycleLogger";

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        logLifecycleState("onCreate", owner.getLifecycle().getCurrentState());
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        logLifecycleState("onStart", owner.getLifecycle().getCurrentState());
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        logLifecycleState("onResume", owner.getLifecycle().getCurrentState());
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        logLifecycleState("onPause", owner.getLifecycle().getCurrentState());
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        logLifecycleState("onStop", owner.getLifecycle().getCurrentState());
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        logLifecycleState("onDestroy", owner.getLifecycle().getCurrentState());
    }

    private void logLifecycleState(String lifecycleCallbackName, Lifecycle.State state) {
        Log.d(TAG, lifecycleCallbackName + ": " + state);
    }
}
