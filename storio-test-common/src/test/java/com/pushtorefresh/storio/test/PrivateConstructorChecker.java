package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class PrivateConstructorChecker<T> {

    @NonNull
    private final Class<T> clazz;

    @Nullable
    private final Class<?> expectedTypeOfException;

    @Nullable
    private final String expectedExceptionMessage;

    private PrivateConstructorChecker(@NonNull Class<T> clazz,
                                      @Nullable Class<?> expectedTypeOfException,
                                      @Nullable String expectedExceptionMessage) {
        this.clazz = clazz;
        this.expectedTypeOfException = expectedTypeOfException;
        this.expectedExceptionMessage = expectedExceptionMessage;
    }

    public static class Builder<T> {

        @NonNull
        private final Class<T> clazz;

        @Nullable
        private Class<?> expectedTypeOfException;

        @Nullable
        private String expectedExceptionMessage;

        Builder(@NonNull Class<T> clazz) {
            this.clazz = clazz;
        }

        @NonNull
        public Builder<T> shouldThrowExceptionOfType(@NonNull Class<?> expectedTypeOfException) {
            this.expectedTypeOfException = expectedTypeOfException;
            return this;
        }

        @NonNull
        public Builder<T> expectedExceptionMessage(@Nullable String expectedExceptionMessage) {
            this.expectedTypeOfException = expectedTypeOfException;
            return this;
        }

        @NonNull
        public PrivateConstructorChecker<T> prepare() {
            if (expectedExceptionMessage != null && expectedTypeOfException == null) {
                throw new IllegalStateException("You can not set expected exception message " +
                        "without expected exception type");
            }

            return new PrivateConstructorChecker<T>(
                    clazz,
                    expectedTypeOfException,
                    expectedExceptionMessage
            );
        }
    }

    @NonNull
    public static <T> Builder<T> forClass(@NonNull Class<T> clazz) {
        return new Builder<T>(clazz);
    }

    public void check() {
        Constructor constructor;

        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Can not get default declared constructor for class = "
                    + clazz,
                    e
            );
        }

        constructor.setAccessible(true);

        if (!Modifier.isPrivate(constructor.getModifiers())) {
            throw new AssertionError("Constructor must be private");
        }

        try {
            constructor.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // Fixed by setAccessible(true)
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            // It's okay case if we expect some exception from this constructor
            if (expectedTypeOfException != null) {
                if (!expectedTypeOfException.equals(e.getClass())) {
                    throw new IllegalStateException("Expected exception of type = "
                            + expectedTypeOfException + ", but was exception of type = "
                            + e
                    );
                }

                if (expectedExceptionMessage != null) {
                    if (!expectedExceptionMessage.equals(e.getMessage())) {
                        throw new IllegalStateException("Expected exception message = '"
                                + expectedExceptionMessage + "', but was = '"
                                + e.getMessage() + "'",
                                e
                        );
                    }
                }

                // Everything is okay
            } else {
                throw new IllegalStateException("No exception was expected", e);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Looks like constructor of " + clazz + " is not default", e);
        }
    }
}
