package com.pushtorefresh.storio.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func0;
import rx.observers.TestSubscriber;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.schedulers.Schedulers.computation;

public class OnSubscribeExecuteAsBlockingTest {

    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        //noinspection unchecked
        final PreparedOperation<String> preparedOperation = mock(PreparedOperation.class);

        final String expectedResult = "expected_string";

        when(preparedOperation.executeAsBlocking())
                .thenReturn(expectedResult);

        final String actualResult = Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(preparedOperation))
                .toBlocking()
                .first();

        verify(preparedOperation, times(1)).executeAsBlocking();
        verify(preparedOperation, times(0)).createObservable();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldNotNotifySubscribeIfItAlreadyUnsubscribed() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final PreparedOperation<String> preparedOperation = new PreparedOperation<String>() {
            @NonNull
            @Override
            public String executeAsBlocking() {
                try {
                    countDownLatch.await(1, MINUTES);
                } catch (InterruptedException e) {
                    // It's okay, RxJava interrupts the thread
                }

                return "finished";
            }

            @NonNull
            @Override
            public Observable<String> createObservable() {
                return Observable
                        .defer(new Func0<Observable<String>>() {
                            @Override
                            public Observable<String> call() {
                                return Observable.just(executeAsBlocking());
                            }
                        });
            }
        };

        //noinspection unchecked
        final TestSubscriber<String> subscriber = new TestSubscriber<String>();

        final Subscription subscription = Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(preparedOperation))
                .subscribeOn(computation())
                .subscribe(subscriber);

        // Now we firstly unsubscribe
        subscription.unsubscribe();

        // Then we releasing latch that locks executeAsBlocking()
        countDownLatch.countDown();

        // If subscriber already unsubscribed — we should not work with it
        subscriber.assertNoValues();
        subscriber.assertNoTerminalEvent();
    }
}
