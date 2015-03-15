package com.pushtorefresh.storio.sample.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import timber.log.Timber;

/**
 * Toasts are not important so much to crash application
 * This class contains "safe" methods for showing toasts
 */
public class ToastHelper {

    private ToastHelper() {
    }

    public static void safeShowToast(@Nullable Context context, @Nullable String message, int length) {
        try {
            if (context != null && !TextUtils.isEmpty(message)) {
                Toast.makeText(context, message, length).show();
            }
        } catch (Exception e) {
            Timber.w("safeShowToast failed", e);
        }
    }

    public static void safeShowToast(@Nullable Context context, @StringRes int stringRes, int length, @Nullable Object... formatArgs) {
        try {
            //noinspection ConstantConditions
            safeShowToast(context, context.getString(stringRes, formatArgs), length);
        } catch (Exception e) {
            Timber.w("safeShowToast failed", e);
        }
    }

    public static void safeShowShortToast(@Nullable Context context, @Nullable String message) {
        safeShowToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void safeShowShortToast(@Nullable Context context, @StringRes int stringRes, @Nullable Object... formatArgs) {
        safeShowToast(context, stringRes, Toast.LENGTH_SHORT, formatArgs);
    }

    public static void safeShowLongToast(@Nullable Context context, @Nullable String message) {
        safeShowToast(context, message, Toast.LENGTH_LONG);
    }

    public static void safeShowLongToast(@Nullable Context context, @StringRes int stringRes, @Nullable Object... formatArgs) {
        safeShowToast(context, stringRes, Toast.LENGTH_LONG, formatArgs);
    }
}