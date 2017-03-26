package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operations.PreparedOperation;

/**
 * Observes, modifies, and potentially short-circuits database operations going out and the
 * corresponding results coming back in.
 * Can be used for logging, caching, extending default functionality with plugins, etc.
 */
public interface Interceptor {

    @Nullable
    <Result> Result intercept(@NonNull PreparedOperation<Result> operation, @NonNull Chain chain);

    /**
     * Encapsulates logic of getting from one interceptor to the next one.
     */
    interface Chain {

        @Nullable
        <Result> Result proceed(@NonNull PreparedOperation<Result> operation);
    }
}