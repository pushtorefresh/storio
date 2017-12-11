package com.pushtorefresh.storio3.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.Interceptor.Chain;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

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

    @Test
    public void buildChain_shouldThrowIfRegisteredInterceptorNull() {
        try {
            final List<Interceptor> interceptors = Arrays.asList(null, mock(Interceptor.class));
            final Chain chain = ChainImpl.buildChain(interceptors, mock(Interceptor.class));
            chain.proceed(mock(PreparedOperation.class));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Interceptor should not be null");
        }
    }

    @Test
    public void buildChain_shouldThrowIfRealInterceptorNull() {
        try {
            final List<Interceptor> interceptors = Collections.singletonList(mock(Interceptor.class));
            //noinspection ConstantConditions
            final Chain chain = ChainImpl.buildChain(interceptors, null);
            chain.proceed(mock(PreparedOperation.class));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Interceptor should not be null");
        }
    }

    @Test
    public void buildChain_placesRealInterceptorAfterRegistered() {
        Interceptor registered1 = spy(new IntermediateInterceptor());
        Interceptor registered2 = spy(new IntermediateInterceptor());
        final List<Interceptor> interceptors = Arrays.asList(registered1, registered2);

        Interceptor real = mock(Interceptor.class);

        final Chain chain = ChainImpl.buildChain(interceptors, real);

        InOrder inOrder = Mockito.inOrder(registered1, registered2, real);

        PreparedOperation operation = mock(PreparedOperation.class);
        chain.proceed(operation);

        inOrder.verify(registered1).intercept(eq(operation), any(Chain.class));
        inOrder.verify(registered2).intercept(eq(operation), any(Chain.class));
        inOrder.verify(real).intercept(eq(operation), any(Chain.class));
    }

    private static class IntermediateInterceptor implements Interceptor {

        @Override
        @Nullable
        public <Result, WrappedResult, Data> Result intercept(
                @NonNull PreparedOperation<Result, WrappedResult, Data> operation,
                @NonNull Chain chain
        ) {
            return chain.proceed(operation);
        }
    }
}