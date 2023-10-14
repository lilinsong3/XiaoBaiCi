package com.github.lilinsong3.xiaobaici;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {

    // 标题
    private final MutableLiveData<String> appBarTitle = new MutableLiveData<>();

    // 底部导航栏
    private final MutableLiveData<Boolean> showBottomView = new MutableLiveData<>(true);
    public final LiveData<Boolean> distinctShowBottomView = Transformations.distinctUntilChanged(showBottomView);

    // 顶部工具栏
    private final MutableLiveData<Boolean> showTopToolbar = new MutableLiveData<>(false);
    public final LiveData<Boolean> distinctShowTopToolbar = Transformations.distinctUntilChanged(showTopToolbar);

    // 加载
    private final MutableLiveData<Boolean> globalLoading = new MutableLiveData<>(false);

    // 两次点击返回退出的毫秒间隔
    private static final Long TWICE_ON_BACK_PRESSED_MILLIS_INTERVAL = 2500L;
    private Long firstOnBackPressedMillis = 0L;

    @Inject
    public MainActivityViewModel() {}

    public LiveData<String> getAppBarTitle() {
        return appBarTitle;
    }

    public void setAppBarTitle(String newTitle) {
        appBarTitle.setValue(newTitle);
    }

    public void makeBottomViewShow(Boolean isShowing) {
        showBottomView.setValue(isShowing);
    }

    public void makeTopToolbarShow(Boolean isShowing) {
        showTopToolbar.setValue(isShowing);
    }

    public void setGlobalLoading(Boolean isLoading) {
        globalLoading.setValue(isLoading);
    }

    public LiveData<Boolean> getGlobalLoading() {
        return globalLoading;
    }

    public void rememberFirstOnBackPressedMillis() {
        firstOnBackPressedMillis = System.currentTimeMillis();
    }

    public boolean canExit() {
        return System.currentTimeMillis() - firstOnBackPressedMillis < TWICE_ON_BACK_PRESSED_MILLIS_INTERVAL;
    }
}
