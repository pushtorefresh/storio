package com.pushtorefresh.storio.test;

import android.os.Build;

import java.lang.reflect.Field;

public class Utils {

    public static final int MAX_SDK_VERSION = getMaxSdkVersion();
    public static final int MIN_SDK_VERSION = 14;

    private Utils() {
        throw new IllegalStateException("No instances please!");
    }

    private static int getMaxSdkVersion() {
        final Field[] versionCodesFields = Build.VERSION_CODES.class.getDeclaredFields();

        // At least 23
        int maxSdkVersion = 23;

        for (final Field versionCodeField : versionCodesFields) {
            versionCodeField.setAccessible(true);

            try {
                final Class<?> fieldType = versionCodeField.getType();

                if (fieldType.equals(Integer.class)) {
                    int sdkVersion = (Integer) versionCodeField.get(null);
                    maxSdkVersion = Math.max(maxSdkVersion, sdkVersion);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return maxSdkVersion;
    }
}
