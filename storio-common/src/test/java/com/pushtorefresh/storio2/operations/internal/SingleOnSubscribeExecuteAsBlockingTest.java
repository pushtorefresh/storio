package com.pushtorefresh.storio2.operations.internal;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.operations.PreparedOperation;

import org.junit.Test;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class SingleOnSubscribeExecuteAsBlockingTest {

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        //noinspection unchecked
        final PreparedOperation<String, String> preparedOperation = mock(PreparedOperation.class);
        String expectedResult = "test";
        when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);

        TestObserver<String> testObserver = new TestObserver<String>();

        verifyZeroInteractions(preparedOperation);

        Single<String> single = Single.create(new SingleOnSubscribeExecuteAsBlocking<String, String>(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        single.subscribe(testObserver);

        testObserver.assertValue(expectedResult);
        testObserver.assertNoErrors();
        testObserver.assertComplete();

        verify(preparedOperation).executeAsBlocking();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        //noinspection unchecked
        final PreparedOperation<Object, Object> preparedOperation = mock(PreparedOperation.class);

        StorIOException expectedException = new StorIOException("test exception");

        when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);

        TestObserver<Object> testObserver = new TestObserver<Object>();

        Single<Object> single = Single.create(new SingleOnSubscribeExecuteAsBlocking<Object, Object>(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        single.subscribe(testObserver);

        testObserver.assertError(expectedException);
        testObserver.assertNotComplete();

        verify(preparedOperation).executeAsBlocking();
    }
}
