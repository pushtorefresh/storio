package com.pushtorefresh.storio.operations.internal;

import com.pushtorefresh.storio.operations.PreparedOperation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.schedulers.Schedulers.io;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Subscriber.class)
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
        //noinspection CheckResult
        verify(preparedOperation, times(0)).asRxObservable();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    public void shouldNotNotifySubscriberIfItsAlreadyUnsubscribed() {
        //noinspection unchecked
        PreparedOperation<Object> preparedOperation = mock(PreparedOperation.class);

        //noinspection unchecked
        Subscriber<Object> subscriber = PowerMockito.mock(Subscriber.class);

        // subscriber.isUnsubscribed() is final, so we use PowerMock to override it
        PowerMockito.when(subscriber.isUnsubscribed()).thenReturn(true);

        OnSubscribeExecuteAsBlocking
                .newInstance(preparedOperation)
                .call(subscriber);

        verify(preparedOperation).executeAsBlocking();

        // Subscriber should not be notified about results of calculations
        verify(subscriber, never()).onNext(any());
        verify(subscriber, never()).onError(any(Throwable.class));
        verify(subscriber, never()).onCompleted();
    }

    @Test
    public void shouldCallExecuteAsBlockingEvenIfSubscriberAlreadyUnsubscribed() {
        //noinspection unchecked
        PreparedOperation<Object> preparedOperation = mock(PreparedOperation.class);

        //noinspection unchecked
        Subscriber<Object> subscriber = PowerMockito.mock(Subscriber.class);

        // subscriber.isUnsubscribed() is final, so we use PowerMock to override it
        PowerMockito.when(subscriber.isUnsubscribed()).thenReturn(true);

        OnSubscribeExecuteAsBlocking
                .newInstance(preparedOperation)
                .call(subscriber);

        // Even if subscriber is unsubscribed when call was done,
        // executeAsBlocking() must be called (for example for Put and Delete operations)
        // But we should think about skipping call to executeAsBlocking() for Get Operation in same case
        verify(preparedOperation).executeAsBlocking();
    }

    @Test
    public void shouldCallOnError() {
        Throwable throwable = new IllegalStateException("Test exception");
        //noinspection unchecked
        PreparedOperation<String> preparedOperation = mock(PreparedOperation.class);
        when(preparedOperation.executeAsBlocking()).thenThrow(throwable);

        TestSubscriber<String> testSubscriber = TestSubscriber.create();

        OnSubscribe<String> onSubscribe = OnSubscribeExecuteAsBlocking.newInstance(preparedOperation);
        Observable.create(onSubscribe).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertError(throwable);
        testSubscriber.assertNoValues();
    }

    @Test
    public void shouldHonorBackpressureWithMultipleSubscribeOn() {
        TestSubscriber<String> testSubscriber = TestSubscriber.create();

        //noinspection unchecked
        PreparedOperation<String> preparedOperation = mock(PreparedOperation.class);
        when(preparedOperation.executeAsBlocking()).thenReturn("b");

        OnSubscribe<String> onSubscribe = OnSubscribeExecuteAsBlocking.newInstance(preparedOperation);

        Observable.just("a")
                .startWith(Observable.create(onSubscribe))
                .subscribeOn(io())
                .subscribeOn(io())   // duplicate
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues("b", "a");
    }
}
