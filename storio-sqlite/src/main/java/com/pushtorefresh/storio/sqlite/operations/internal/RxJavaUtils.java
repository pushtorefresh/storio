package com.pushtorefresh.storio.sqlite.operations.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlockingCompletable;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlockingSingle;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

public final class RxJavaUtils {

    private RxJavaUtils() {
        throw new IllegalStateException("No instances please.");
    }

    @CheckResult
    @NonNull
    public static <T> Observable<T> createObservable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<T> operation
    ) {
        throwExceptionIfRxJavaIsNotAvailable("asRxObservable()");

        final Observable<T> observable =
                Observable.create(OnSubscribeExecuteAsBlocking.newInstance(operation));

        return subscribeOn(storIOSQLite, observable);
    }

    @CheckResult
    @NonNull
    public static <T> Single<T> createSingle(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<T> operation
    ) {
        throwExceptionIfRxJavaIsNotAvailable("asRxSingle()");

        final Single<T> single =
                Single.create(OnSubscribeExecuteAsBlockingSingle.newInstance(operation));

        return subscribeOn(storIOSQLite, single);
    }

    @CheckResult
    @NonNull
    public static <T> Completable createCompletable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<T> operation
    ) {
        throwExceptionIfRxJavaIsNotAvailable("asRxCompletable()");

        final Completable completable =
                Completable.create(OnSubscribeExecuteAsBlockingCompletable.newInstance(operation));

        return subscribeOn(storIOSQLite, completable);
    }

    @CheckResult
    @NonNull
    public static <T> Observable<T> subscribeOn(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull Observable<T> observable
    ) {
        final Scheduler scheduler = storIOSQLite.defaultScheduler();
        return scheduler != null ? observable.subscribeOn(scheduler) : observable;
    }

    @CheckResult
    @NonNull
    public static <T> Single<T> subscribeOn(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull Single<T> single
    ) {
        final Scheduler scheduler = storIOSQLite.defaultScheduler();
        return scheduler != null ? single.subscribeOn(scheduler) : single;
    }

    @CheckResult
    @NonNull
    public static Completable subscribeOn(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull Completable completable
    ) {
        final Scheduler scheduler = storIOSQLite.defaultScheduler();
        return scheduler != null ? completable.subscribeOn(scheduler) : completable;
    }
}
