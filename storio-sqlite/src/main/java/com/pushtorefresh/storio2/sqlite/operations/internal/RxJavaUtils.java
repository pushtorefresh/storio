package com.pushtorefresh.storio2.sqlite.operations.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.operations.internal.CompletableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.operations.internal.FlowableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.operations.internal.SingleOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

import static com.pushtorefresh.storio2.internal.Environment.throwExceptionIfRxJava2IsNotAvailable;

public final class RxJavaUtils {

    private RxJavaUtils() {
        throw new IllegalStateException("No instances please.");
    }

    @CheckResult
    @NonNull
    public static <T, Data> Flowable<T> createFlowable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<T, Data> operation,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxObservable()");

        return subscribeOn(
                storIOSQLite,
                Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<T, Data>(operation), backpressureStrategy)
        );
    }

    @CheckResult
    @NonNull
    public static <T, Data> Single<T> createSingle(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<T, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxSingle()");

        final Single<T> single =
                Single.create(new SingleOnSubscribeExecuteAsBlocking<T, Data>(operation));

        return subscribeOn(storIOSQLite, single);
    }

    @CheckResult
    @NonNull
    public static <T, Data> Completable createCompletable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<T, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxCompletable()");

        final Completable completable =
                Completable.create(new CompletableOnSubscribeExecuteAsBlocking(operation));

        return subscribeOn(storIOSQLite, completable);
    }

    @CheckResult
    @NonNull
    public static <T> Flowable<T> subscribeOn(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull Flowable<T> flowable
    ) {
        final Scheduler scheduler = storIOSQLite.defaultRxScheduler();
        return scheduler != null ? flowable.subscribeOn(scheduler) : flowable;
    }

    @CheckResult
    @NonNull
    public static <T> Single<T> subscribeOn(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull Single<T> single
    ) {
        final Scheduler scheduler = storIOSQLite.defaultRxScheduler();
        return scheduler != null ? single.subscribeOn(scheduler) : single;
    }

    @CheckResult
    @NonNull
    public static Completable subscribeOn(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull Completable completable
    ) {
        final Scheduler scheduler = storIOSQLite.defaultRxScheduler();
        return scheduler != null ? completable.subscribeOn(scheduler) : completable;
    }
}
