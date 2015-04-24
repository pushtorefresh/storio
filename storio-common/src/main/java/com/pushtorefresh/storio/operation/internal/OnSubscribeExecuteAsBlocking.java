package com.pushtorefresh.storio.operation.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.PreparedOperation;

import rx.Observable;
import rx.Subscriber;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p/>
 * For internal usage only!
 */
public class OnSubscribeExecuteAsBlocking<Result> implements Observable.OnSubscribe<Result> {

    @NonNull
    private final PreparedOperation<Result> preparedOperation;

    private OnSubscribeExecuteAsBlocking(@NonNull PreparedOperation<Result> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    /**
     * Creates new instance of {@link OnSubscribeExecuteAsBlocking}
     *
     * @param preparedOperation non-null instance of {@link PreparedOperation} which will be used to provide result to subscribers
     * @param <Result>          type of result of {@link PreparedOperation}
     * @return new instance of {@link OnSubscribeExecuteAsBlocking}
     */
    @NonNull
    public static <Result> Observable.OnSubscribe<Result> newInstance(@NonNull PreparedOperation<Result> preparedOperation) {
        return new OnSubscribeExecuteAsBlocking<Result>(preparedOperation);
    }

    @Override
    public void call(Subscriber<? super Result> subscriber) {
        final Result result = preparedOperation.executeAsBlocking();

        if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(result);
            subscriber.onCompleted();
        }
    }
}
