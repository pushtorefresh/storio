package com.pushtorefresh.storio.util;

public class EnvironmentUtil {

    /**
     * True if RxJava is on classpath, false otherwise
     */
    public static final boolean HAS_RX_JAVA = hasRxJava();

    private EnvironmentUtil() { }

    // thanks Retrofit for that piece of code
    private static boolean hasRxJava() {
        try {
            Class.forName("rx.Observable");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
