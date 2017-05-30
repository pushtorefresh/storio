package com.pushtorefresh.storio2.internal;

import android.support.annotation.NonNull;

/**
 * For internal use only!
 */
public final class Environment {

    /**
     * True if RxJava 2.x is on classpath, false otherwise
     */
    public static final boolean RX_JAVA_2_IS_IN_THE_CLASS_PATH = isRxJava2InTheClassPath();

    private Environment() {
        throw new IllegalStateException("No instances please");
    }

    // Thanks Retrofit for that piece of code.
    private static boolean isRxJava2InTheClassPath() {
        try {
            Class.forName("io.reactivex.Observable");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Throws RuntimeException if RxJava is not available
     *
     * @param messagePrefix first part of exception message, for example: "Creating Observable"
     */
    public static void throwExceptionIfRxJava2IsNotAvailable(@NonNull String messagePrefix) {
        if (RX_JAVA_2_IS_IN_THE_CLASS_PATH == false) {
            throw new IllegalStateException(messagePrefix + " requires RxJava in classpath, please add it as compile dependency to the application");
        }
    }
}
