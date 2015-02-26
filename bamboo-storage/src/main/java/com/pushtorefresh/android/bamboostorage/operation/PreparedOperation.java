package com.pushtorefresh.android.bamboostorage.operation;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Common API of all prepared operations
 *
 * @param <Result> type of result
 */
public interface PreparedOperation<Result> {

    /**
     * Should execute operation synchronously in current thread
     *
     * @return result of operation
     */
    @NonNull Result executeAsBlocking();

    /**
     * Should create {@link rx.Observable}
     *
     * @return result of operation
     */
    @NonNull Observable<Result> createObservable();
}
