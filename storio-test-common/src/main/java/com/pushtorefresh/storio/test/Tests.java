package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class Tests {

    public static void assertThatConstructorIsPrivateAndThrowsException(@NonNull Class<?> clazz, @NonNull Throwable expectedThrowable) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();

            if (!Modifier.isPrivate(constructor.getModifiers())) {
                throw new AssertionError("Constructor must be private");
            }

            constructor.setAccessible(true);

            try {
                constructor.newInstance();
                throw new AssertionError("Constructor must be private");
            } catch (InvocationTargetException expected) {
                Throwable cause = expected.getCause();

                if (!cause.getClass().equals(expectedThrowable.getClass())) {
                    throw new AssertionError("Constructor thrown exception with " +
                            "unexpected type: expected = " + expected + ", actual = " + cause);
                }

                if (!expectedThrowable.getMessage().equals(cause.getMessage())) {
                    throw new AssertionError("Constructor thrown exception with " +
                            "unexpected message: expected = " + expectedThrowable.getMessage() + ", actual = " + cause.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
