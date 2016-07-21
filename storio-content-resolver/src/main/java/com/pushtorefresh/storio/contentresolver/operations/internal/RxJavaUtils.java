package com.pushtorefresh.storio.contentresolver.operations.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlockingCompletable;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlockingSingle;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

public class RxJavaUtils {

    private RxJavaUtils() {
        throw new IllegalStateException("No instances please.");
    }

    @CheckResult
    @NonNull
    public static <T> Observable<T> createObservable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T> operation
    ) {
        throwExceptionIfRxJavaIsNotAvailable("asRxObservable()");

        final Observable<T> observable =
                Observable.create(OnSubscribeExecuteAsBlocking.newInstance(operation));
        
        return subscribeOn(storIOContentResolver, observable);
    }

    @CheckResult
    @NonNull
    public static <T> Single<T> createSingle(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T> operation
    ) {
        throwExceptionIfRxJavaIsNotAvailable("asRxSingle()");

        final Single<T> single =
                Single.create(OnSubscribeExecuteAsBlockingSingle.newInstance(operation));

        return subscribeOn(storIOContentResolver, single);
    }

    @CheckResult
    @NonNull
    public static <T> Completable createCompletable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T> operation
    ) {
        throwExceptionIfRxJavaIsNotAvailable("asRxCompletable()");

        final Completable completable =
                Completable.create(OnSubscribeExecuteAsBlockingCompletable.newInstance(operation));

        return subscribeOn(storIOContentResolver, completable);
    }

    @CheckResult
    @NonNull
    public static <T> Observable<T> subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Observable<T> observable
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultScheduler();
        return scheduler != null ? observable.subscribeOn(scheduler) : observable;
    }

    @CheckResult
    @NonNull
    public static <T> Single<T> subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Single<T> single
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultScheduler();
        return scheduler != null ? single.subscribeOn(scheduler) : single;
    }

    @CheckResult
    @NonNull
    public static Completable subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Completable completable
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultScheduler();
        return scheduler != null ? completable.subscribeOn(scheduler) : completable;
    }
}