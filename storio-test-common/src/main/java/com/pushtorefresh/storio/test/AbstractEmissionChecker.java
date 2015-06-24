package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rx.Subscription;

public abstract class AbstractEmissionChecker<T> {

    @NonNull
    private final Queue<T> expectedValues;

    @NonNull
    private final AtomicBoolean expectedValueReceived = new AtomicBoolean(false);

    @NonNull
    private final AtomicReference<Throwable> onNextObtainedThrowable = new AtomicReference<Throwable>(null);

    public AbstractEmissionChecker(@NonNull Queue<T> expectedValues) {
        this.expectedValues = new ConcurrentLinkedQueue<T>(expectedValues);
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

        Throwable problem = onNextObtainedThrowable.get();

        while (!expectedValueReceived.get()
                && problem == null
                && !(System.currentTimeMillis() - startTime > timeoutMillis)) {
            Thread.yield();
            problem = onNextObtainedThrowable.get();
        }

        final Throwable throwable = onNextObtainedThrowable.get();

        if (throwable != null) {
           throw new AssertionError("Throwable occurred while waiting: " + throwable);
        } else if (!expectedValueReceived.get()) {
            throw new AssertionError("Expected value = " + expectedValues.peek() + " was not received, " +
                    "timeout = " + timeoutMillis + "ms, expectedValues.size = " + expectedValues.size());
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
        try {
            final T expectedItem = expectedValues.remove();

            if (!expectedItem.equals(obtained)) {
                throw new AssertionError("Obtained item not equals to expected: obtained = "
                        + obtained + ", expected = " + expectedItem);
            } else if (expectedValueReceived.get()) {
                throw new AssertionError("Incorrect state");
            }

            expectedValueReceived.set(true);
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
