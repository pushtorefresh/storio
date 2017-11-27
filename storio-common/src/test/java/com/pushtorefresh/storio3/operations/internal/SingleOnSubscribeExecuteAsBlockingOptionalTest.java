package com.pushtorefresh.storio3.operations.internal;

import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import org.junit.Test;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class SingleOnSubscribeExecuteAsBlockingOptionalTest {

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        //noinspection unchecked
        final PreparedOperation<String, Optional<String>, String> preparedOperation = mock(PreparedOperation.class);
        String expectedResult = "test";
        when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);

        TestObserver<Optional<String>> testObserver = new TestObserver<Optional<String>>();

        verifyZeroInteractions(preparedOperation);

        Single<Optional<String>> single =
                Single.create(new SingleOnSubscribeExecuteAsBlockingOptional<String, String>(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        single.subscribe(testObserver);

        testObserver.assertValue(Optional.of(expectedResult));
        testObserver.assertNoErrors();
        testObserver.assertComplete();

        verify(preparedOperation).executeAsBlocking();
    }

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldCallOnErrorIfExceptionOccurred() {
        //noinspection unchecked
        final PreparedOperation<Object, Optional<Object>, Object> preparedOperation = mock(PreparedOperation.class);

        StorIOException expectedException = new StorIOException("test exception");

        when(preparedOperation.executeAsBlocking()).thenThrow(expectedException);

        TestObserver<Optional<Object>> testObserver = new TestObserver<Optional<Object>>();

        Single<Optional<Object>> single =
                Single.create(new SingleOnSubscribeExecuteAsBlockingOptional<Object, Object>(preparedOperation));

        verifyZeroInteractions(preparedOperation);

        single.subscribe(testObserver);

        testObserver.assertError(expectedException);
        testObserver.assertNotComplete();

        verify(preparedOperation).executeAsBlocking();
    }
}
