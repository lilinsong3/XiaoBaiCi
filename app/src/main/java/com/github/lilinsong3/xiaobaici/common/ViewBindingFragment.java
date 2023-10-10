package com.github.lilinsong3.xiaobaici.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class ViewBindingFragment<VB extends ViewBinding> extends Fragment {

    protected VB viewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = initViewBinding(inflater,container);
        return viewBinding.getRoot();
    }

    public abstract VB initViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }
}
