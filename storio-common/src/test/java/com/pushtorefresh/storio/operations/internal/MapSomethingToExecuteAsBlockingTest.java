package com.pushtorefresh.storio.operations.internal;

import com.pushtorefresh.storio.operations.PreparedOperation;

import org.junit.Test;

import rx.Observable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapSomethingToExecuteAsBlockingTest {

    @SuppressWarnings("unchecked")
    @Test
    public void verifyBehavior() {
        final PreparedOperation<String> preparedOperation = mock(PreparedOperation.class);

        final String expectedMapResult = "expected_string";

        when(preparedOperation.executeAsBlocking())
                .thenReturn(expectedMapResult);

        final String actualMapResult = Observable.just(1)
                .map(MapSomethingToExecuteAsBlocking.newInstance(preparedOperation))
                .toBlocking()
                .first();

        verify(preparedOperation, times(1)).executeAsBlocking();
        verify(preparedOperation, times(0)).asRxObservable();

        assertThat(actualMapResult).isEqualTo(expectedMapResult);
    }
}
