package com.pushtorefresh.storio.operations.internal;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.operations.PreparedOperation;

import org.junit.Test;

import rx.Single;
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
}
