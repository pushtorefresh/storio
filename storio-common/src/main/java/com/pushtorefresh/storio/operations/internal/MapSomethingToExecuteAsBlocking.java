package com.pushtorefresh.storio.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;

import rx.functions.Func1;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class MapSomethingToExecuteAsBlocking<Something, Result> implements Func1<Something, Result> {

    @NonNull
    private final PreparedOperation<Result> preparedOperation;

    private MapSomethingToExecuteAsBlocking(@NonNull PreparedOperation<Result> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    /**
     * Creates new instance of {@link MapSomethingToExecuteAsBlocking}
     *
     * @param preparedOperation non-null instance of {@link PreparedOperation} which will be used to react on calls to rx function
     * @param <Something>       type of map argument
     * @param <Result>          type of result of rx map function
     * @return new instance of {@link MapSomethingToExecuteAsBlocking}
     */
    @NonNull
    public static <Something, Result> Func1<Something, Result> newInstance(@NonNull PreparedOperation<Result> preparedOperation) {
        return new MapSomethingToExecuteAsBlocking<Something, Result>(preparedOperation);
    }

    @Override
    public Result call(Something changes) {
        return preparedOperation.executeAsBlocking();
    }
}
