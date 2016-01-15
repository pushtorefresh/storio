package com.pushtorefresh.storio.operations;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import rx.Observable;
import rx.Single;

/**
 * Common API of all prepared operations
 *
 * @param <Result> type of result
 */
public interface PreparedOperation<Result> {

    /**
     * Executes operation synchronously in current thread.
     * <p>
     * Notice: Blocking I/O operation should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, execute blocking I/O operation only from background thread.
     * See {@link WorkerThread}.
     *
     * @return nullable result of operation.
     */
    @Nullable
    @WorkerThread
    Result executeAsBlocking();

    /**
     * Creates {@link rx.Observable} that emits result of Operation.
     * <p>
     * Observable may be "Hot" or "Cold", please read documentation of the concrete implementation.
     *
     * @return observable result of operation with only one {@link rx.Observer#onNext(Object)} call.
     * @deprecated (will be removed in 2.0) please use {@link #asRxObservable()}.
     */
    @NonNull
    @CheckResult
    @Deprecated
    Observable<Result> createObservable();

    /**
     * Creates {@link rx.Observable} that emits result of Operation.
     * <p>
     * Observable may be "Hot" (usually "Warm") or "Cold", please read documentation of the concrete implementation.
     *
     * @return observable result of operation with only one {@link rx.Observer#onNext(Object)} call.
     */
    @NonNull
    @CheckResult
    Observable<Result> asRxObservable();

    /**
     * Creates {@link rx.Single} that emits result of Operation lazily when somebody subscribes to it.
     * <p>
     *
     * @return single result of operation.
     */
    @NonNull
    @CheckResult
    Single<Result> asRxSingle();
}
