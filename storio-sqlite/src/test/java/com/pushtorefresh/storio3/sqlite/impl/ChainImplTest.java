package com.pushtorefresh.storio3.sqlite.impl;

import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.sqlite.Interceptor;
import com.pushtorefresh.storio3.sqlite.Interceptor.Chain;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ChainImplTest {

    @Test
    public void proceed_shouldThrowIfIteratorEmpty() {
        try {
            final List<Interceptor> empty = Collections.emptyList();
            final Chain chain = new ChainImpl(empty.listIterator());
            chain.proceed(mock(PreparedOperation.class));
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("proceed was called on empty iterator");
        }
    }

    @Test
    public void proceed_shouldThrowIfCalledMultipleTimes() {
        final List<Interceptor> interceptors = Arrays.asList(mock(Interceptor.class), mock(Interceptor.class));
        try {
            final Chain chain = new ChainImpl(interceptors.listIterator());
            final PreparedOperation operation = mock(PreparedOperation.class);
            chain.proceed(operation);
            chain.proceed(operation);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e)
                    .hasMessage("nextInterceptor " + interceptors.get(0) + " must call proceed() exactly once");
        }
    }
}