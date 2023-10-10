package com.github.lilinsong3.xiaobaici.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.lilinsong3.xiaobaici.ui.hanziword.HanziWordFragment;

import java.util.List;

public class HanziWordFragmentStateAdapter extends FragmentStateAdapter {

    private static final String TAG = "HanziWordFragmentStateAdapter";

    private final AsyncListDiffer<Long> itemListDiffer = new AsyncListDiffer<>(this, DIFF_CALLBACK);

    public HanziWordFragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        HanziWordFragment hanziWordFragment = new HanziWordFragment();
        Bundle bundleArgs = new Bundle();
        bundleArgs.putLong(HanziWordFragment.HANZI_WORD_BUNDLE_KEY, itemListDiffer.getCurrentList().get(position));
        hanziWordFragment.setArguments(bundleArgs);
        return hanziWordFragment;
    }

    @Override
    public int getItemCount() {
        return itemListDiffer.getCurrentList().size();
    }

    @Override
    public long getItemId(int position) {
        return itemListDiffer.getCurrentList().get(position);
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemListDiffer.getCurrentList().contains(itemId);
    }

    public void submit(@NonNull List<Long> data, @NonNull final Runnable commitCallback) {
        if (data.isEmpty()) {
            return;
        }
        itemListDiffer.submitList(data, commitCallback);
    }

    public static final DiffUtil.ItemCallback<Long> DIFF_CALLBACK = new DiffUtil.ItemCallback<Long>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull Long oldId,  @NonNull Long newId) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldId.equals(newId);
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull Long oldId,  @NonNull Long newId) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldId.equals(newId);
        }
    };
}
