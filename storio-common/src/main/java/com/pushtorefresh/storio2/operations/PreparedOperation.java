package com.pushtorefresh.storio2.operations;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Common API of all prepared operations
 *
 * @param <Result> type of result
 */
public interface PreparedOperation<Result, Data> {

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
     * Creates {@link io.reactivex.Flowable} that emits result of Operation.
     * <p>
     * Flowable may be "Hot" (usually "Warm") or "Cold", please read documentation of the concrete implementation.
     */
    @NonNull
    @CheckResult
    Flowable<Result> asRxFlowable(BackpressureStrategy backpressureStrategy);

    /**
     * Creates {@link io.reactivex.Single} that emits result of Operation lazily when somebody subscribes to it.
     * <p>
     *
     * @return single result of operation.
     */
    @NonNull
    @CheckResult
    Single<Result> asRxSingle();

    @NonNull
    Data getData();

}
