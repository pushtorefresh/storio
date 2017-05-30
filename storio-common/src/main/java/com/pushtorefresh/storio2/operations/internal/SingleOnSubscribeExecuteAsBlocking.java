package com.pushtorefresh.storio2.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.operations.PreparedOperation;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class SingleOnSubscribeExecuteAsBlocking<Result, Data> implements SingleOnSubscribe<Result> {

    @NonNull
    private final PreparedOperation<Result, Data> preparedOperation;

    public SingleOnSubscribeExecuteAsBlocking(@NonNull PreparedOperation<Result, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    public void subscribe(@io.reactivex.annotations.NonNull SingleEmitter<Result> emitter) throws Exception {
        try {
            emitter.onSuccess(preparedOperation.executeAsBlocking());
        } catch (Exception e) {
            emitter.onError(e);
        }
    }
}
