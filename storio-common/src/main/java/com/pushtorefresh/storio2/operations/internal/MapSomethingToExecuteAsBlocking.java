package com.pushtorefresh.storio2.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.operations.PreparedOperation;

import io.reactivex.functions.Function;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class MapSomethingToExecuteAsBlocking<Something, Result, Data> implements Function<Something, Result> {

    @NonNull
    private final PreparedOperation<Result, Data> preparedOperation;

    public MapSomethingToExecuteAsBlocking(@NonNull PreparedOperation<Result, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    public Result apply(@io.reactivex.annotations.NonNull Something something) throws Exception {
        return preparedOperation.executeAsBlocking();
    }
}
