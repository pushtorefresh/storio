package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public abstract class AbstractEmissionChecker<T> {

    @NonNull
    private final Queue<T> expectedValues;

    @NonNull
    private final AtomicBoolean allExpectedValuesReceived = new AtomicBoolean(false);

    @NonNull
    private final AtomicInteger receivedCounter = new AtomicInteger(0);

    private Subscription subscription;

    public AbstractEmissionChecker(@NonNull Queue<T> expectedValues) {
        this.expectedValues = new ConcurrentLinkedQueue<T>(expectedValues);
    }

    /**
     * Returns timeout of assertion for {@link #waitAllAndUnsubscribe()}.
     *
     * @return timeout in millis.
     */
    protected long timeoutMillis() {
        return 15000; // 15 seconds
    }

    @NonNull
    public abstract Observable<T> newObservable();

    public void beginSubscription() {
        if (subscription != null) {
            throw new IllegalStateException("Already subscribed");
        }
        subscription = newObservable().subscribe(new Action1<T>() {
            @Override public void call(T obtained) {
                onNextObtained(obtained);
            }
        });
    }

    /**
     * Checks that new received value is expected.
     *
     * @param obtained new value.
     */
    protected void onNextObtained(@NonNull T obtained) {
        final T expectedItem = expectedValues.remove();
        receivedCounter.incrementAndGet();

        if (!expectedItem.equals(obtained)) {
            throw new AssertionError("Obtained item not equals to expected: obtained = "
                    + obtained + ", expected = " + expectedItem);
        }

        if (allExpectedValuesReceived.get()) {
            throw new AssertionError("Incorrect state");
        }

        allExpectedValuesReceived.set(expectedValues.isEmpty());
    }

    /**
     * Asserts that next expected value was received.
     */
    public void waitOne() {
        if (subscription == null) {
            throw new IllegalStateException("Not subscribed");
        }

        final int receivedBefore = receivedCounter.get();
        final long startTime = System.currentTimeMillis();
        final long timeoutMillis = timeoutMillis();

        while (receivedBefore == receivedCounter.get()
                && !(System.currentTimeMillis() - startTime > timeoutMillis)) {
            Thread.yield();
        }

        if (receivedBefore == receivedCounter.get()) {
            throw new AssertionError("Expected value = " + expectedValues.peek() + " was not received, " +
                    "timeout = " + timeoutMillis + "ms");
        }
    }

    /**
     * Asserts that all expected values were received.
     */
    public void waitAllAndUnsubscribe() {
        if (subscription == null) {
            throw new IllegalStateException("Not subscribed");
        }

        final long startTime = System.currentTimeMillis(); // We can not use SystemClock here :( Not in class path
        final long timeoutMillis = timeoutMillis();

        while (!allExpectedValuesReceived.get()
                && !(System.currentTimeMillis() - startTime > timeoutMillis)) {
            Thread.yield();
        }

        if (!allExpectedValuesReceived.get()) {
            throw new AssertionError("Expected value = " + expectedValues.peek() + " was not received, " +
                    "timeout = " + timeoutMillis + "ms");
        }
        
        subscription.unsubscribe();
        subscription = null;
    }
}