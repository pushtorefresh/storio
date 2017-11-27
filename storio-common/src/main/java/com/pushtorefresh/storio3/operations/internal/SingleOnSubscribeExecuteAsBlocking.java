package com.pushtorefresh.storio3.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.operations.PreparedOperation;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class SingleOnSubscribeExecuteAsBlocking<Result, WrappedResult, Data> implements SingleOnSubscribe<Result> {

    @NonNull
    private final PreparedOperation<Result, WrappedResult, Data> preparedOperation;

    public SingleOnSubscribeExecuteAsBlocking(@NonNull PreparedOperation<Result, WrappedResult, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<Result> emitter) throws Exception {
        try {
            final Result value = preparedOperation.executeAsBlocking();
            emitter.onSuccess(value);
        } catch (Exception e) {
            emitter.onError(e);
        }
    }
}
