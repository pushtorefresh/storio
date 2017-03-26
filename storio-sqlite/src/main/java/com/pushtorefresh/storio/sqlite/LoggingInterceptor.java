package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pushtorefresh.storio.operations.PreparedOperation;

import java.util.Locale;

public class LoggingInterceptor implements Interceptor {

    @NonNull
    public static LoggingInterceptor defaultLogger() {
        return new LoggingInterceptor(new Logger() {
            @Override
            public void log(@NonNull String message) {
                Log.d("StorIO", message);
            }
        });
    }

    @NonNull
    public static LoggingInterceptor withLogger(@NonNull Logger logger) {
        return new LoggingInterceptor(logger);
    }

    @NonNull
    private final Logger logger;

    private LoggingInterceptor(@NonNull Logger logger) {
        this.logger = logger;
    }

    @Nullable
    @Override
    public <Result> Result intercept(@NonNull PreparedOperation<Result> operation, @NonNull Chain chain) {
        final long startMillis = System.currentTimeMillis();

        final Result result = chain.proceed(operation);

        final long endMillis = System.currentTimeMillis();
        logger.log(
                String.format(
                        Locale.US,
                        "%s\n=> data: %s\n<= result: %s\ntook %dms",
                        operation.getClass().getSimpleName(),
                        operation.getData(),
                        result,
                        endMillis - startMillis
                )
        );

        return result;
    }

    public interface Logger {

        void log(@NonNull String message);
    }
}
