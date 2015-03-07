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
     * Executes operation synchronously in current thread
     *
     * @return result of operation
     */
    @NonNull Result executeAsBlocking();

    /**
     * Creates {@link rx.Observable} that emits only one result
     *
     * @return result of operation
     */
    @NonNull Observable<Result> createObservable();
}
