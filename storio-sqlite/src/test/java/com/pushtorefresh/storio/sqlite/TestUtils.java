package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;

import java.util.List;

import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Utility class for common methods, that check exceptions with cause.
 */
public class TestUtils {

    private TestUtils() {
        //No instances please
    }

    public static <T> void checkException(
            @NonNull final TestSubscriber<T> testSubscriber,
            @NonNull final Class expectedClass,
            @NonNull final Class causeClass,
            @NonNull final String causeMessage) {

        checkException(testSubscriber, expectedClass, causeClass);
        final Throwable expected = testSubscriber.getOnErrorEvents().get(0);
        final Throwable cause = expected.getCause();
        assertEquals(causeMessage, cause.getMessage());
    }

    public static <T> void checkException(
            @NonNull final TestSubscriber<T> testSubscriber,
            @NonNull final Class expectedClass,
            @NonNull final Class causeClass) {

        final List<Throwable> errors = testSubscriber.getOnErrorEvents();
        assertEquals(1, errors.size());

        final Throwable expected = errors.get(0);
        assertTrue(expectedClass.isInstance(expected));

        final Throwable cause = expected.getCause();
        assertNotNull(cause);
        assertTrue(causeClass.isInstance(cause));
    }

    public static void checkException(
            @NonNull final Throwable throwable,
            @NonNull final Class causeClass,
            @NonNull final String causeMessage) {

        checkException(throwable, causeClass);
        final Throwable cause = throwable.getCause();
        assertEquals(causeMessage, cause.getMessage());
    }

    public static void checkException(
            @NonNull final Throwable throwable,
            @NonNull final Class causeClass) {

        final Throwable cause = throwable.getCause();
        assertNotNull(cause);
        assertTrue(causeClass.isInstance(cause));
    }
}
