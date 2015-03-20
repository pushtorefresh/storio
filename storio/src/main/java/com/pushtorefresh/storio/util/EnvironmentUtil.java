package com.pushtorefresh.storio.util;

import android.support.annotation.NonNull;

public class EnvironmentUtil {

    /**
     * True if RxJava is on classpath, false otherwise
     */
    public static final boolean IS_RX_JAVA_AVAILABLE = hasRxJava();

    private EnvironmentUtil() {
    }

    // thanks Retrofit for that piece of code
    private static boolean hasRxJava() {
        try {
            Class.forName("rx.Observable");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Creates exception for notifying about "No RxJava in classpath"
     *
     * @param messagePrefix first part of exception message, for example: "Observing changes in StorIOSQLiteDb"
     * @return RuntimeException with message about "No RxJava in classpath"
     */
    @NonNull
    public static IllegalStateException newRxJavaIsNotAvailableException(@NonNull String messagePrefix) {
        return new IllegalStateException(messagePrefix + " requires RxJava in classpath, please add it as compile dependency to the application");
    }

    /**
     * Throws RuntimeException if RxJava is not available
     *
     * @param messagePrefix first part of exception message, for example: "Creating Observable"
     */
    public static void throwExceptionIfRxJavaIsNotAvailable(@NonNull String messagePrefix) {
        if (!IS_RX_JAVA_AVAILABLE) {
            throw newRxJavaIsNotAvailableException(messagePrefix);
        }
    }
}
