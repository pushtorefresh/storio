package com.pushtorefresh.storio2.sqlite;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.operations.PreparedOperation;

/**
 * Observes, modifies, and potentially short-circuits database operations going out and the
 * corresponding results coming back in.
 * Can be used for logging, caching, extending default functionality with plugins, etc.
 */
public interface Interceptor {

    @NonNull
    <Result, Data> Result intercept(@NonNull PreparedOperation<Result, Data> operation, @NonNull Chain chain);

    /**
     * Encapsulates logic of proceeding from one interceptor to another.
     */
    interface Chain {

        @NonNull
        <Result, Data> Result proceed(@NonNull PreparedOperation<Result, Data> operation);
    }
}