package com.pushtorefresh.storio.operations.internal;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.operations.PreparedOperation;

import org.junit.Test;

import rx.Single;
import rx.SingleSubscriber;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class OnSubscribeExecuteAsBlockingSingleTest {

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        //noinspection unchecked
        final PreparedOperation<Object> preparedOperation = mock(PreparedOperation.class);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();

        verifyZeroInteractions(preparedOperation);

        Single<Object> single = Single.create(OnSubscribeExecuteAsBlockingSingle.newInstance(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        single.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        verify(preparedOperation).executeAsBlocking();
        verify(preparedOperation, never()).asRxObservable();
        verify(preparedOperation, never()).asRxSingle();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        //noinspection unchecked
        final PreparedOperation<Object> preparedOperation = mock(PreparedOperation.class);

        StorIOException expectedException = new StorIOException("test exception");

        when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();

        Single<Object> single = Single.create(OnSubscribeExecuteAsBlockingSingle.newInstance(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        single.subscribe(testSubscriber);

        testSubscriber.assertError(expectedException);
        testSubscriber.assertTerminalEvent();

        verify(preparedOperation).executeAsBlocking();
        verify(preparedOperation, never()).asRxSingle();
        verify(preparedOperation, never()).asRxObservable();
    }

    @Test
    public void shouldCallExecuteAsBlockingEvenIfSubscriberAlreadyUnsubscribed() {
        //noinspection unchecked
        PreparedOperation<Object> preparedOperation = mock(PreparedOperation.class);

        //noinspection unchecked
        SingleSubscriber<Object> subscriber = new SingleSubscriber() {

            @Override
            public void onSuccess(Object value) {
                // nothing
            }

            @Override
            public void onError(Throwable error) {
                // nothing
            }
        };

        subscriber.unsubscribe();

        OnSubscribeExecuteAsBlockingSingle
                .newInstance(preparedOperation)
                .call(subscriber);

        // Even if subscriber is unsubscribed when call was done,
        // executeAsBlocking() must be called (for example for Put and Delete operations)
        // But we should think about skipping call to executeAsBlocking() for Get Operation in same case
        verify(preparedOperation).executeAsBlocking();
    }
}
