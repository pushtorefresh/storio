package com.pushtorefresh.storio2.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.Optional;
import com.pushtorefresh.storio2.operations.PreparedOperation;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class SingleOnSubscribeExecuteAsBlockingOptional<Result, Data> implements SingleOnSubscribe<Optional<Result>> {

    @NonNull
    private final PreparedOperation<Result, Optional<Result>, Data> preparedOperation;

    public SingleOnSubscribeExecuteAsBlockingOptional(@NonNull PreparedOperation<Result, Optional<Result>, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    public void subscribe(@NonNull SingleEmitter<Optional<Result>> emitter) throws Exception {
        try {
            final Result value = preparedOperation.executeAsBlocking();
            emitter.onSuccess(Optional.of(value));
        } catch (Exception e) {
            emitter.onError(e);
        }
    }
}
