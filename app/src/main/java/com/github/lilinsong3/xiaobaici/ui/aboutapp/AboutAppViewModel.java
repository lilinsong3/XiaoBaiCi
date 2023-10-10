package com.github.lilinsong3.xiaobaici.ui.aboutapp;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutAppViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(true);

    public void setLoading(Boolean isLoading) {
        loading.setValue(isLoading);
    }

}
