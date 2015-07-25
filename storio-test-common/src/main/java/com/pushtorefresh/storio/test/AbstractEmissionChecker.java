package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.Queue;

import rx.Subscription;

public abstract class AbstractEmissionChecker<T> {

    @NonNull
    private final Queue<T> expectedValues;

    @NonNull
    private final Queue<T> obtainedValues;

    @NonNull
    private final Object lock = new Object();

    public AbstractEmissionChecker(@NonNull Queue<T> expectedValues) {
        this.expectedValues = new LinkedList<T>(expectedValues);
        this.obtainedValues = new LinkedList<T>();
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
        final T expected;

        synchronized (lock) {
            expected = expectedValues.peek();
        }

        boolean expectedValueWasReceived = false;

        while (!expectedValueWasReceived
                && !(System.currentTimeMillis() - startTime > timeoutMillis)) {

            synchronized (lock) {
                if (obtainedValues.size() > 0) {
                    final T obtained = obtainedValues.remove();

                    if (expected.equals(obtained)) {
                        expectedValues.remove();
                        expectedValueWasReceived = true;
                    } else {
                        throw new AssertionError("Obtained item not equals to expected: obtained = "
                                + obtained + ", expected = " + expected);
                    }
                }
            }

            Thread.yield(); // let other threads work
        }

        if (!expectedValueWasReceived) {
            throw new AssertionError("Expected value = " + expected + " was not received, " +
                    "timeout = " + timeoutMillis + "ms, expectedValues.size = " + expectedValues.size() + ", obtainedValues = " + obtainedValues);
        }
    }

    /**
     * Asserts that all expected values were received.
     */
    public void assertThatNoExpectedValuesLeft() {
        synchronized (lock) {
            if (expectedValues.size() != 0) {
                throw new AssertionError("Not all expected values were received: queue = " + expectedValues);
            }
        }
    }

    /**
     * Checks that new received value is expected.
     *
     * @param obtained new value.
     */
    protected void onNextObtained(@NonNull T obtained) {
        synchronized (lock) {
            if (expectedValues.size() == 0) {
                throw new IllegalStateException("Received emission, but no more " +
                        "emissions were expected: obtained " + obtained +
                        ", expectedValues = " + expectedValues +
                        ", obtainedValues = " + obtainedValues);
            }

            obtainedValues.add(obtained);
        }
    }

    @NonNull
    public abstract Subscription subscribe();
}
