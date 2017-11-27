package com.pushtorefresh.storio3.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.operations.PreparedOperation;

import io.reactivex.functions.Function;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class MapSomethingToExecuteAsBlocking<Something, Result, WrappedResult, Data> implements Function<Something, Result> {

    @NonNull
    private final PreparedOperation<Result, WrappedResult, Data> preparedOperation;

    public MapSomethingToExecuteAsBlocking(@NonNull PreparedOperation<Result, WrappedResult, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    public Result apply(@NonNull Something something) throws Exception {
        return preparedOperation.executeAsBlocking();
    }
}
