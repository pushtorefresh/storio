package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

    public static <T> void checkToString(@NonNull T object) {
        try {//noinspection unchecked
            final Class<T> clazz = (Class<T>) object.getClass();

            final String toString = object.toString();

            final Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                if (field.getName().equals("$jacocoData")) {
                    // Jacoco coverage tool can add field for internal usage
                    // It wont affect release code, of course.
                    continue;
                }

                if (!toString.contains(field.getName() + "=" + field.get(object).toString())) {
                    throw new AssertionError("toString() does not contain field = "
                            + field.getName() + ", object = " + object);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
