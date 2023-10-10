package com.github.lilinsong3;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

import com.github.lilinsong3.xiaobaici.XiaoBaiCiApplication;

public final class AppTestRunner extends AndroidJUnitRunner {

    // setup for Hilt application test
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, XiaoBaiCiApplication.class.getName(), context);
    }
}
