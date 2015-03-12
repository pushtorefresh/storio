package com.pushtorefresh.android.bamboostorage.db.operation;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Common API for prepared operations that can return "reactive stream"
 *
 * @param <Result> type of result
 */
public interface PreparedOperationWithReactiveStream<Result> extends PreparedOperation<Result> {

    /**
     * Creates {@link rx.Observable} that will be subscribed to changes of the result
     * Such Observable will be immediately notified with first result of operation
     * and will be subscribed to changes of the result in future,
     * so Observer of this Observable should be ready to receive multiple {@link rx.Observer#onNext(Object)}
     * callbacks
     *
     * @return observable result which will be notified about changes of result in future
     */
    @NonNull Observable<Result> createObservableStream();
}
