package com.pushtorefresh.storio3.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class FlowableOnSubscribeExecuteAsBlockingOptional<Result, Data> implements FlowableOnSubscribe<Optional<Result>> {

    @NonNull
    private final PreparedOperation<Result, Optional<Result>, Data> preparedOperation;

    public FlowableOnSubscribeExecuteAsBlockingOptional(@NonNull PreparedOperation<Result, Optional<Result>, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<Optional<Result>> emitter) throws Exception {
        try {
            final Result value = preparedOperation.executeAsBlocking();
            emitter.onNext(Optional.of(value));
            emitter.onComplete();
        } catch (Exception e) {
            emitter.onError(e);
        }
    }
}
