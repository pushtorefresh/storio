package com.pushtorefresh.storio2.operations.internal;

import com.pushtorefresh.storio2.operations.PreparedOperation;

import org.junit.Test;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapSomethingToExecuteAsBlockingTest {

    @SuppressWarnings("unchecked")
    @Test
    public void verifyBehavior() {
        final PreparedOperation<String, Object> preparedOperation = mock(PreparedOperation.class);

        final String expectedMapResult = "expected_string";

        when(preparedOperation.executeAsBlocking())
                .thenReturn(expectedMapResult);

        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();

        Flowable
                .just(1)
                .map(new MapSomethingToExecuteAsBlocking<Integer, String, Object>(preparedOperation))
                .subscribe(testSubscriber);

        verify(preparedOperation, times(1)).executeAsBlocking();

        testSubscriber.assertValue(expectedMapResult);
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }
}
