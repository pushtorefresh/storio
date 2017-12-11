package com.pushtorefresh.storio3;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.operations.PreparedOperation;

/**
 * Observes, modifies, and potentially short-circuits database operations going out and the
 * corresponding results coming back in.
 * Can be used for logging, caching, extending default functionality with plugins, etc.
 */
public interface Interceptor {

    <Result, WrappedResult, Data> Result intercept(@NonNull PreparedOperation<Result, WrappedResult, Data> operation, @NonNull Chain chain);

    /**
     * Encapsulates logic of proceeding from one interceptor to another.
     */
    interface Chain {

        <Result, WrappedResult, Data> Result proceed(@NonNull PreparedOperation<Result, WrappedResult, Data> operation);
    }
}