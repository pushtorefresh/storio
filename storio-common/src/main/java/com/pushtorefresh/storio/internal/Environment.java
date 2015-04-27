package com.pushtorefresh.storio.internal;

import android.support.annotation.NonNull;

/**
 * For internal usage only!
 */
public final class Environment {

    /**
     * True if RxJava is on classpath, false otherwise
     */
    public static final boolean IS_RX_JAVA_AVAILABLE = isRxJavaAvailable();

    private Environment() {
        throw new IllegalStateException("No instances please");
    }

    // thanks Retrofit for that piece of code
    private static boolean isRxJavaAvailable() {
        try {
            Class.forName("rx.Observable");
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
    public static void throwExceptionIfRxJavaIsNotAvailable(@NonNull String messagePrefix) {
        if (!IS_RX_JAVA_AVAILABLE) {
            throw new IllegalStateException(messagePrefix + " requires RxJava in classpath, please add it as compile dependency to the application");
        }
    }
}
