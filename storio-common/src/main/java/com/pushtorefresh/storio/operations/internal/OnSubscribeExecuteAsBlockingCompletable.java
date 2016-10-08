package com.pushtorefresh.storio.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.operations.PreparedOperation;

import rx.Completable;
import rx.CompletableSubscriber;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class OnSubscribeExecuteAsBlockingCompletable implements Completable.OnSubscribe {

    @NonNull
    private final PreparedOperation preparedOperation;

    private OnSubscribeExecuteAsBlockingCompletable(@NonNull PreparedOperation preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    /**
     * Creates new instance of {@link OnSubscribeExecuteAsBlockingCompletable}
     *
     * @param preparedOperation non-null instance of {@link PreparedOperation} which will be used to provide result to subscribers
     * @return new instance of {@link OnSubscribeExecuteAsBlockingCompletable}
     */
    @NonNull
    public static Completable.OnSubscribe newInstance(@NonNull PreparedOperation preparedOperation) {
        return new OnSubscribeExecuteAsBlockingCompletable(preparedOperation);
    }

    @Override
    public void call(@NonNull CompletableSubscriber subscriber) {
        try {
            preparedOperation.executeAsBlocking();

            subscriber.onCompleted();
        } catch (StorIOException e) {
            subscriber.onError(e);
        }
    }
}
