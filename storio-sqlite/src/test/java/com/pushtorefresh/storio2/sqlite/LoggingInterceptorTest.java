package com.pushtorefresh.storio2.sqlite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pushtorefresh.storio2.operations.PreparedOperation;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class LoggingInterceptorTest {

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private LoggingInterceptor loggingInterceptor;

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private StringBuilder resultBuilder;

    @SuppressWarnings("NullableProblems") // @Before
    @NonNull
    private static StringBuilder androidLogBuilder;

    @Before
    public void beforeEachTest() {
        androidLogBuilder = new StringBuilder();
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

    @Config(shadows = {ShadowLog.class})
    @Test
    public void defaultLoggerShouldLogToAndroidLog() {
        loggingInterceptor = LoggingInterceptor.defaultLogger();
        final String result = "some result";
        final String data = "some data";
        final Interceptor.Chain chain = new TestChain(result);
        final PreparedOperation operation = new TestOperation(data);

        loggingInterceptor.intercept(operation, chain);

        // TODO how to test timings?
        assertThat(androidLogBuilder.toString()).startsWith(
                "StorIO:TestOperation\n=> data: some data\n<= result: some result\ntook "
        );
    }

    @Implements(Log.class)
    public static class ShadowLog {

        @Implementation
        public static int d(@NonNull String tag, @NonNull String msg) {
            androidLogBuilder.append(tag).append(":").append(msg);
            return 0;
        }
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
        public Flowable<String> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
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
