package com.pushtorefresh.storio3.operations.internal;

import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.operations.PreparedMaybeOperation;

import org.junit.Test;

import io.reactivex.Maybe;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MaybeOnSubscribeExecuteAsBlockingTest {

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        //noinspection unchecked
        final PreparedMaybeOperation<String, String, String> preparedOperation = mock(PreparedMaybeOperation.class);
        String expectedResult = "test";
        when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);

        TestObserver<String> testObserver = new TestObserver<String>();

        verifyZeroInteractions(preparedOperation);

        Maybe<String> maybe = Maybe.create(new MaybeOnSubscribeExecuteAsBlocking<String, String, String>(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        maybe.subscribe(testObserver);

        testObserver.assertValue(expectedResult);
        testObserver.assertNoErrors();
        testObserver.assertComplete();

        verify(preparedOperation).executeAsBlocking();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCompleteIfNullOccurred() {
        //noinspection unchecked
        final PreparedMaybeOperation<String, String, String> preparedOperation = mock(PreparedMaybeOperation.class);
        when(preparedOperation.executeAsBlocking()).thenReturn(null);

        TestObserver<String> testObserver = new TestObserver<String>();

        verifyZeroInteractions(preparedOperation);

        Maybe<String> maybe = Maybe.create(new MaybeOnSubscribeExecuteAsBlocking<String, String, String>(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        maybe.subscribe(testObserver);

        testObserver.assertNoErrors();
        testObserver.assertComplete();

        verify(preparedOperation).executeAsBlocking();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        //noinspection unchecked
        final PreparedMaybeOperation<Object, Object, Object> preparedOperation = mock(PreparedMaybeOperation.class);

        StorIOException expectedException = new StorIOException("test exception");

        when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);

        TestObserver<Object> testObserver = new TestObserver<Object>();

        Maybe<Object> maybe = Maybe.create(new MaybeOnSubscribeExecuteAsBlocking<Object, Object, Object>(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        maybe.subscribe(testObserver);

        testObserver.assertError(expectedException);
        testObserver.assertNotComplete();

        verify(preparedOperation).executeAsBlocking();
    }
}
