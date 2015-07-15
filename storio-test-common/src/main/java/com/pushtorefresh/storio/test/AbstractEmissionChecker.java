package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import rx.Subscription;

public abstract class AbstractEmissionChecker<T> {

    @NonNull
    private final Queue<T> expectedValues;

    @NonNull
    private final Queue<T> obtainedValues;

    @NonNull
    private final AtomicReference<Throwable> onNextObtainedThrowable = new AtomicReference<Throwable>(null);

    public AbstractEmissionChecker(@NonNull Queue<T> expectedValues) {
        this.expectedValues = new ConcurrentLinkedQueue<T>(expectedValues);
        this.obtainedValues = new ConcurrentLinkedQueue<T>();
    }

    /**
     * Returns timeout of assertion for {@link #awaitNextExpectedValue()}.
     *
     * @return timeout in millis.
     */
    protected long timeoutMillis() {
        return 60000; // 60 seconds
    }

    /**
     * Asserts that next expected value was received.
     */
    public void awaitNextExpectedValue() {
        final long startTime = System.currentTimeMillis(); // We can not use SystemClock here :( Not in class path
        final long timeoutMillis = timeoutMillis();
        final T expected = expectedValues.remove();

        boolean expectedValueWasReceived = false;

        while (!expectedValueWasReceived
                && onNextObtainedThrowable.get() == null
                && !(System.currentTimeMillis() - startTime > timeoutMillis)) {

            if (obtainedValues.size() > 0) {
                final T obtained = obtainedValues.remove();

                if (expected.equals(obtained)) {
                    expectedValueWasReceived = true;
                } else {
                    throw new AssertionError("Obtained item not equals to expected: obtained = "
                            + obtained + ", expected = " + expected);
                }
            }

            Thread.yield(); // let other threads work
        }

        if (onNextObtainedThrowable.get() != null) {
           throw new AssertionError("Throwable occurred while waiting: " + onNextObtainedThrowable.get());
        } else if (!expectedValueWasReceived) {
            throw new AssertionError("Expected value = " + expected + " was not received, " +
                    "timeout = " + timeoutMillis + "ms, expectedValues.size = " + expectedValues.size() + ", obtainedValues = " + obtainedValues);
        }
    }

    /**
     * Asserts that all expected values were received.
     */
    public void assertThatNoExpectedValuesLeft() {
        if (expectedValues.size() != 0) {
            throw new AssertionError("Not all expected values were received: queue = " + expectedValues);
        }
    }

    /**
     * Checks that new received value is expected.
     *
     * @param obtained new value.
     */
    protected void onNextObtained(@NonNull T obtained) {
        try {
            obtainedValues.add(obtained);
        } catch (Throwable throwable) {
            // Catch everything, it's not a bug, it's a feature
            // Really, we don't want to break contract of Emission Checker if something goes wrong
            // Because problem can be handled via rx.Observable's Subscriber
            // And if so -> it'll break behavior of Emission Checker
            onNextObtainedThrowable.set(throwable);
        }
    }

    @NonNull
    public abstract Subscription subscribe();
}
