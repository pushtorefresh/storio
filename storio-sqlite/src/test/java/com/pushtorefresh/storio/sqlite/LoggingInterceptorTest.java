package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetObject;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoggingInterceptorTest {

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private LoggingInterceptor loggingInterceptor;

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private StringBuilder resultBuilder;

    @Before
    public void beforeEachTest() {
        resultBuilder = new StringBuilder();

        loggingInterceptor = new LoggingInterceptor(new LoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                resultBuilder.append(message).append("\n");
            }
        });
    }

    @Test
    public void interceptShouldLogToLogger() {
        final Interceptor.Chain chain = mock(Interceptor.Chain.class);
        final PreparedOperation operation = mock(PreparedGetObject.class);
        final String result = "some result";
        final String data = "some data";
        when(chain.proceed(operation)).thenReturn(result);
        when(operation.getData()).thenReturn(data);

        loggingInterceptor.intercept(operation, chain);

        // TODO how to test timings?
        assertThat(resultBuilder.toString()).startsWith(
                "PreparedGetObject\n=> data: some data\n<= result: some result\ntook "
        );
    }
}