package com.pushtorefresh.storio2.operations.internal;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.operations.PreparedWriteOperation;

import org.junit.Test;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CompletableOnSubscribeExecuteAsBlockingTest {

    @SuppressWarnings("ResourceType")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        final PreparedWriteOperation preparedOperation = mock(PreparedWriteOperation.class);

        TestObserver testObserver = new TestObserver();

        verifyZeroInteractions(preparedOperation);

        Completable completable = Completable.create(new CompletableOnSubscribeExecuteAsBlocking(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        completable.subscribe(testObserver);

        testObserver.assertNoErrors();
        testObserver.assertComplete();

        verify(preparedOperation).executeAsBlocking();
        verify(preparedOperation, never()).asRxFlowable(any(BackpressureStrategy.class));
        verify(preparedOperation, never()).asRxSingle();
        verify(preparedOperation, never()).asRxCompletable();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown", "ResourceType"})
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        final PreparedWriteOperation preparedOperation = mock(PreparedWriteOperation.class);

        StorIOException expectedException = new StorIOException("test exception");

        when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);

        TestObserver testObserver = new TestObserver();

        Completable completable = Completable.create(new CompletableOnSubscribeExecuteAsBlocking(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        completable.subscribe(testObserver);

        testObserver.assertError(expectedException);
        testObserver.assertNotComplete();

        verify(preparedOperation).executeAsBlocking();
        verify(preparedOperation, never()).asRxFlowable(any(BackpressureStrategy.class));
        verify(preparedOperation, never()).asRxSingle();
        verify(preparedOperation, never()).asRxCompletable();
    }
}
