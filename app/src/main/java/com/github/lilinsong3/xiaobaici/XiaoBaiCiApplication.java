package com.github.lilinsong3.xiaobaici;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class XiaoBaiCiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        boolean isNightMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
//                .getBoolean(SettingsFragment.NIGHT_SWITCH_PREFERENCE_KEY, false);
//        int currentNightMode = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        if (isNightMode) {
//            switch (currentNightMode) {
//                case AppCompatDelegate.MODE_NIGHT_YES:
//                break;
//                case AppCompatDelegate.MODE_NIGHT_NO:
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            }
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
