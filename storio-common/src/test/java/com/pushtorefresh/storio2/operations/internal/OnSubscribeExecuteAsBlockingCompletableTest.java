package com.pushtorefresh.storio2.operations.internal;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.operations.PreparedWriteOperation;

import org.junit.Test;

import rx.Completable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class OnSubscribeExecuteAsBlockingCompletableTest {

    @SuppressWarnings("ResourceType")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        final PreparedWriteOperation preparedOperation = mock(PreparedWriteOperation.class);

        TestSubscriber testSubscriber = new TestSubscriber();

        verifyZeroInteractions(preparedOperation);

        Completable completable = Completable.create(OnSubscribeExecuteAsBlockingCompletable.newInstance(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        completable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        verify(preparedOperation).executeAsBlocking();
        verify(preparedOperation, never()).asRxObservable();
        verify(preparedOperation, never()).asRxSingle();
        verify(preparedOperation, never()).asRxCompletable();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown", "ResourceType"})
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        final PreparedWriteOperation preparedOperation = mock(PreparedWriteOperation.class);

        StorIOException expectedException = new StorIOException("test exception");

        when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);

        TestSubscriber testSubscriber = new TestSubscriber();

        Completable completable = Completable.create(OnSubscribeExecuteAsBlockingCompletable.newInstance(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        completable.subscribe(testSubscriber);

        testSubscriber.assertError(expectedException);
        testSubscriber.assertTerminalEvent();

        verify(preparedOperation).executeAsBlocking();
        verify(preparedOperation, never()).asRxObservable();
        verify(preparedOperation, never()).asRxSingle();
        verify(preparedOperation, never()).asRxCompletable();
    }
}
