package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Subscription;

public abstract class AbstractEmissionChecker<T> {

    @NonNull
    private final Queue<T> expectedValues;

    @NonNull
    private final AtomicBoolean expectedValueReceived = new AtomicBoolean(false);

    public AbstractEmissionChecker(@NonNull Queue<T> expectedValues) {
        this.expectedValues = new ConcurrentLinkedQueue<T>(expectedValues);
    }

    /**
     * Returns timeout of assertion for {@link #assertThatNextExpectedValueReceived()}.
     *
     * @return timeout in millis.
     */
    protected long timeoutMillis() {
        return 15000; // 15 seconds
    }

    /**
     * Asserts that next expected value was received.
     */
    public void assertThatNextExpectedValueReceived() {
        final long startTime = System.currentTimeMillis(); // We can not use SystemClock here :( Not in class path
        final long timeoutMillis = timeoutMillis();

        while (!expectedValueReceived.get()
                && !(System.currentTimeMillis() - startTime > timeoutMillis)) {
            Thread.yield();
        }

        if (!expectedValueReceived.get()) {
            throw new AssertionError("Expected value = " + expectedValues.peek() + " was not received, " +
                    "timeout = " + timeoutMillis + "ms");
        } else {
            // reset flag
            expectedValueReceived.set(false);
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
        final T expectedItem = expectedValues.remove();

        if (!expectedItem.equals(obtained)) {
            throw new AssertionError("Obtained item not equals to expected: obtained = "
                    + obtained + ", expected = " + expectedItem);
        }

        if (expectedValueReceived.get()) {
            throw new AssertionError("Incorrect state");
        }

        expectedValueReceived.set(true);
    }

    // TODO: Refactor, probably better to provide Observable itself.
    @NonNull
    public abstract Subscription subscribe();
}