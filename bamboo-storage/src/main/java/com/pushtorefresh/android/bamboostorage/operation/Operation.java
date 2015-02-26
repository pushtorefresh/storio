package com.pushtorefresh.android.bamboostorage.operation;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Guarantees similar API of all operations
 * @param <Result> type of result
 */
interface Operation<Result> {

    /**
     * Should execute operation synchronously in current thread
     * @return result of operation
     */
    @NonNull Result executeAsBlocking();

    /**
     * Should create {@link rx.Observable}
     * @return result of operation
     */
    @NonNull Observable<Result> createObservable();
}
