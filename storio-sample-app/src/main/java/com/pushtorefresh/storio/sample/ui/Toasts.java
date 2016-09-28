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
public class Toasts {

    private Toasts() {
    }

    public static void safeShowToast(@Nullable Context context, @Nullable String message, int length) {
        if (context == null) {
            Timber.w("Toast '%s' skipped, context is null", message);
        } else if (TextUtils.isEmpty(message)) {
            Timber.w("Unable to show toast with empty message");
        } else {
            Toast.makeText(context, message, length).show();
        }
    }

    public static void safeShowToast(@Nullable Context context, @StringRes int stringRes, int length, @Nullable Object... formatArgs) {
        if (context != null) {
            safeShowToast(context, context.getString(stringRes, formatArgs), length);
        } else {
            Timber.w("Toast skipped, context is null");
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