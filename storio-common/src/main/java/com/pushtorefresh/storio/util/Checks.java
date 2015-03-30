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
}
