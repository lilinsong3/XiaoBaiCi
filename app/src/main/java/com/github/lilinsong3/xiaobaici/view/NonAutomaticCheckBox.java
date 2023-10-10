package com.github.lilinsong3.xiaobaici.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 点击时不会自动改变状态
 */

public class NonAutomaticCheckBox extends androidx.appcompat.widget.AppCompatCheckBox {

    private Boolean isPerformingClick = false;

    public NonAutomaticCheckBox(@NonNull Context context) {
        super(context);
    }

    public NonAutomaticCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NonAutomaticCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 如果是通过点击触发的，则不改变状态
     */
    @Override
    public void toggle() {
        if (isPerformingClick) return;
        super.toggle();
    }

    /**
     * 执行点击时不会自动调用 {@link #toggle()}
     * @return 是否处理了点击
     */

    @Override
    public boolean performClick() {
        isPerformingClick = true;
        final boolean handled = super.performClick();
        isPerformingClick = !handled;
        return handled;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return NonAutomaticCheckBox.class.getName();
    }
}
