package com.pushtorefresh.storio.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Checks {

    private Checks() {

    }

    /**
     * Checks that passed reference is not null,
     * throws {@link NullPointerException} with passed message if reference is null
     *
     * @param object  to check
     * @param message exception message if object is null
     */
    public static void checkNotNull(@Nullable Object object, @NonNull String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    /**
     * Checks that passed string is not null and not empty,
     * throws {@link IllegalStateException} with passed message
     * if string is null or empty.
     *
     * @param value   a string to check
     * @param message exception message if object is null
     */
    public static void checkNotEmpty(@Nullable String value, @NonNull String message) {
        if (value == null || value.length() == 0) {
            throw new IllegalStateException(message);
        }
    }
}