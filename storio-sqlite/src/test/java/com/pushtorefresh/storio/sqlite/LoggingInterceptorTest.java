package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operations.PreparedOperation;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import rx.Observable;
import rx.Single;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
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

        loggingInterceptor = LoggingInterceptor.withLogger(new LoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                resultBuilder.append(message).append("\n");
            }
        });
    }

    @Test
    public void interceptShouldLogToLogger() {
        final String result = "some result";
        final String data = "some data";
        final Interceptor.Chain chain = new TestChain(result);
        final PreparedOperation operation = new TestOperation(data);

        loggingInterceptor.intercept(operation, chain);

        // TODO how to test timings?
        assertThat(resultBuilder.toString()).startsWith(
                "TestOperation\n=> data: some data\n<= result: some result\ntook "
        );
    }

    private static class TestChain implements Interceptor.Chain {

        @NonNull
        private final String result;

        private TestChain(@NonNull String result) {
            this.result = result;
        }

        @Nullable
        @Override
        public <Result, Data> Result proceed(@NonNull PreparedOperation<Result, Data> operation) {
            return (Result) result;
        }
    }

    private static class TestOperation implements PreparedOperation<String, String> {

        @NonNull
        private final String data;

        private TestOperation(@NonNull String data) {
            this.data = data;
        }

        @Nullable
        @Override
        public String executeAsBlocking() {
            throw new NotImplementedException();
        }

        @NonNull
        @Override
        public Observable<String> createObservable() {
            throw new NotImplementedException();
        }

        @NonNull
        @Override
        public Observable<String> asRxObservable() {
            throw new NotImplementedException();
        }

        @NonNull
        @Override
        public Single<String> asRxSingle() {
            throw new NotImplementedException();
        }

        @NonNull
        @Override
        public String getData() {
            return data;
        }
    }
}