package com.pushtorefresh.storio.operations;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import rx.Observable;

/**
 * Common API of all prepared operations
 *
 * @param <Result> type of result
 */
public interface PreparedOperation<Result> {

    /**
     * Executes operation synchronously in current thread.
     * <p/>
     * Notice: Blocking I/O operation should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, execute blocking I/O operation only from background thread.
     * See {@link WorkerThread}.
     *
     * @return non-null result of operation.
     */
    @NonNull
    @WorkerThread
    Result executeAsBlocking();

    /**
     * Creates {@link rx.Observable} that emits result of Operation.
     * <p/>
     * Observable may be "Hot" or "Cold", please read documentation of the concrete implementation.
     *
     * @return observable result of operation with only one {@link rx.Observer#onNext(Object)} call.
     */
    @NonNull
    Observable<Result> createObservable();
}
