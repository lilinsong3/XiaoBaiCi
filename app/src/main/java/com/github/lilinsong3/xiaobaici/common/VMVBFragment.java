package com.github.lilinsong3.xiaobaici.common;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

public abstract class VMVBFragment<VM extends ViewModel, VB extends ViewBinding> extends ViewBindingFragment<VB> {
    protected VM viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(provideVMClass());
        setupViewData();
    }

    protected abstract void setupViewData();

    /**
     * 懒得反射
     * @return ViewModel的class对象
     */
    @NonNull
    protected abstract Class<VM> provideVMClass();
}
