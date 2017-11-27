package com.pushtorefresh.storio3.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.operations.PreparedMaybeOperation;

import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class MaybeOnSubscribeExecuteAsBlocking<Result, WrappedResult, Data> implements MaybeOnSubscribe<Result> {

    @NonNull
    private final PreparedMaybeOperation<Result, WrappedResult, Data> preparedOperation;

    public MaybeOnSubscribeExecuteAsBlocking(@NonNull PreparedMaybeOperation<Result, WrappedResult, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    public void subscribe(@NonNull MaybeEmitter<Result> emitter) throws Exception {
        try {
            final Result value = preparedOperation.executeAsBlocking();
            if (value != null) {
                emitter.onSuccess(value);
            } else {
                emitter.onComplete();
            }
        } catch (Exception e) {
            emitter.onError(e);
        }
    }
}
