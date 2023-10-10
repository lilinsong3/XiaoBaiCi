package com.github.lilinsong3.xiaobaici.ui.home;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.color.MaterialColors;

public class HanziWordPageTransformer implements ViewPager2.PageTransformer {

    private static final float LEFT_ENDPOINT = -1f;
    private static final float RIGHT_ENDPOINT = 1f;
    private static final float CENTER_ENDPOINT = 0f;
    private static final float INVISIBLE_ALPHA = 0f;
    private static final float FULLY_VISIBLE_ALPHA = 1f;
    private static final float DEFAULT_TRANSLATION = 0f;
    private static final int ALPHA_WEAKEN_MULTIPLE = 4;
    private static final float Z_TRANSLATION_UNDER = -1f;
    private static final float Z_TRANSLATION_BASE = 0f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        // 页面滑动变化
        ViewPager2 pager = requireViewPager(page);
        if (CENTER_ENDPOINT < position && position <= RIGHT_ENDPOINT) { // (CENTER_ENDPOINT,RIGHT_ENDPOINT]
            if (pager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                page.setTranslationX(page.getWidth() * -position);
            } else {
                page.setTranslationY(page.getHeight() * -position);
            }
            page.setTranslationZ(Z_TRANSLATION_UNDER);
            page.setBackgroundColor(MaterialColors.getColor(page, com.google.android.material.R.attr.colorSurfaceVariant));
        } else { // [-Infinity,CENTER_ENDPOINT]∪(RIGHT_ENDPOINT,Infinity]
            if (pager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                page.setTranslationX(DEFAULT_TRANSLATION);
            } else {
                page.setTranslationY(DEFAULT_TRANSLATION);
            }
            page.setTranslationZ(Z_TRANSLATION_BASE);
            page.setBackgroundColor(MaterialColors.getColor(page, com.google.android.material.R.attr.colorSurface));
        }

        // 收藏按钮透明变化
//        View favoriteView = page.findViewById(R.id.hw_cb_favorite);
//        if (LEFT_ENDPOINT <= position && position < CENTER_ENDPOINT) { // [LEFT_ENDPOINT, CENTER_ENDPOINT)
//            favoriteView.setAlpha((FULLY_VISIBLE_ALPHA - Math.abs(position)) / ALPHA_WEAKEN_MULTIPLE);
//
//        } else if (position == CENTER_ENDPOINT) { // {CENTER_ENDPOINT}
//            favoriteView.setAlpha(FULLY_VISIBLE_ALPHA);
//        } else { // [-Infinity,LEFT_ENDPOINT)∪(CENTER_ENDPOINT, Infinity]
//            favoriteView.setAlpha(INVISIBLE_ALPHA);
//        }
    }

    private ViewPager2 requireViewPager(@NonNull View page) {
        ViewParent parent = page.getParent();
        ViewParent parentParent = parent.getParent();

        if (parent instanceof RecyclerView && parentParent instanceof ViewPager2) {
            return (ViewPager2) parentParent;
        }

        throw new IllegalStateException(
                "Expected the page view to be managed by a ViewPager2 instance.");
    }
}
