package com.github.lilinsong3.xiaobaici.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.LifecycleOwner;

import com.github.lilinsong3.xiaobaici.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;

public abstract class ViewUtil {
    public static void showTextIfNonNull(TextView textView, CharSequence text) {
        if (text == null) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public static void openNormalDialog(
            @NonNull Context context,
            @NonNull LifecycleOwner lifecycleOwner,
            @StringRes int titleId,
            @StringRes int messageId,
            @NonNull Completable onConfirmCompletable
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(titleId)
                .setMessage(messageId)
                .setNegativeButton(R.string.short_cancel, (dialog, which) -> dialog.cancel())
                .setPositiveButton(R.string.short_sure, (dialog, which) -> {
                    dialog.dismiss();
                    haveAToastAfterCleanup(
                            onConfirmCompletable,
                            context,
                            lifecycleOwner
                    );
                }).show();
    }

    public static void haveAToastAfterCleanup(
            @NonNull Completable completable,
            @NonNull Context context,
            @NonNull LifecycleOwner lifecycleOwner
    ) {
        completable.observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> Toast.makeText(context, R.string.long_operation_success, Toast.LENGTH_SHORT))
                .doOnError(e -> Toast.makeText(context, R.string.long_operation_failed, Toast.LENGTH_SHORT))
                .to(RxUtil.autoDispose(lifecycleOwner))
                .subscribe();
    }

    @FunctionalInterface
    public interface OnItemClickListener<D> {
        void onClick(D itemData);
    }

    @FunctionalInterface
    public interface OnClickAction {
        void onClick();
    }
}
