package com.pushtorefresh.storio2.contentresolver.operations.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.operations.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.operations.internal.OnSubscribeExecuteAsBlockingCompletable;
import com.pushtorefresh.storio2.operations.internal.OnSubscribeExecuteAsBlockingSingle;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

import static com.pushtorefresh.storio2.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

public class RxJavaUtils {

    private RxJavaUtils() {
        throw new IllegalStateException("No instances please.");
    }

    @CheckResult
    @NonNull
    public static <T, Data> Observable<T> createObservable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T, Data> operation
    ) {
        throwExceptionIfRxJavaIsNotAvailable("asRxObservable()");

        final Observable<T> observable =
                Observable.create(OnSubscribeExecuteAsBlocking.newInstance(operation));
        
        return subscribeOn(storIOContentResolver, observable);
    }

    @CheckResult
    @NonNull
    public static <T, Data> Single<T> createSingle(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T, Data> operation
    ) {
        throwExceptionIfRxJavaIsNotAvailable("asRxSingle()");

        final Single<T> single =
                Single.create(OnSubscribeExecuteAsBlockingSingle.newInstance(operation));

        return subscribeOn(storIOContentResolver, single);
    }

    @CheckResult
    @NonNull
    public static <T, Data> Completable createCompletable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T, Data> operation
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