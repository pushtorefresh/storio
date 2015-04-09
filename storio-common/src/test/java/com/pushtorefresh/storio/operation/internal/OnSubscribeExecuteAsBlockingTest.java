package com.pushtorefresh.storio.operation.internal;

import com.pushtorefresh.storio.operation.PreparedOperation;

import org.junit.Test;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OnSubscribeExecuteAsBlockingTest {

    @SuppressWarnings("unchecked")
    @Test
    public void verifyBehavior() {
        final PreparedOperation<String> preparedOperation = mock(PreparedOperation.class);

        final String expectedResult = "expected_string";

        when(preparedOperation.executeAsBlocking())
                .thenReturn(expectedResult);

        final String actualResult = Observable.create(OnSubscribeExecuteAsBlocking.newInstance(preparedOperation))
                .toBlocking()
                .first();

        verify(preparedOperation, times(1)).executeAsBlocking();
        verify(preparedOperation, times(0)).createObservable();

        assertEquals(expectedResult, actualResult);
    }
}
