package com.pushtorefresh.storio.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;

import rx.Single;
import rx.SingleSubscriber;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class OnSubscribeExecuteAsBlockingSingle<Result, Data> implements Single.OnSubscribe<Result> {

    @NonNull
    private final PreparedOperation<Result, Data> preparedOperation;

    private OnSubscribeExecuteAsBlockingSingle(@NonNull PreparedOperation<Result, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    /**
     * Creates new instance of {@link OnSubscribeExecuteAsBlockingSingle}
     *
     * @param preparedOperation non-null instance of {@link PreparedOperation} which will be used to provide result to subscribers
     * @param <Result>          type of result of {@link PreparedOperation}
     * @return new instance of {@link OnSubscribeExecuteAsBlockingSingle}
     */
    @NonNull
    public static <Result, Data> Single.OnSubscribe<Result> newInstance(@NonNull PreparedOperation<Result, Data> preparedOperation) {
        return new OnSubscribeExecuteAsBlockingSingle<Result, Data>(preparedOperation);
    }

    @Override
    public void call(SingleSubscriber<? super Result> singleSubscriber) {
        final Result result = preparedOperation.executeAsBlocking();

        if (!singleSubscriber.isUnsubscribed()) {
            singleSubscriber.onSuccess(result);
        }
    }
}
