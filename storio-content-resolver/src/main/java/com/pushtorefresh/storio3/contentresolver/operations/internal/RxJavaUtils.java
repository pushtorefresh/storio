package com.pushtorefresh.storio3.contentresolver.operations.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.contentresolver.Changes;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import com.pushtorefresh.storio3.operations.PreparedCompletableOperation;
import com.pushtorefresh.storio3.operations.PreparedMaybeOperation;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.operations.internal.CompletableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.FlowableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.FlowableOnSubscribeExecuteAsBlockingOptional;
import com.pushtorefresh.storio3.operations.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.MapSomethingToExecuteAsBlockingOptional;
import com.pushtorefresh.storio3.operations.internal.MaybeOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.SingleOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.SingleOnSubscribeExecuteAsBlockingOptional;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.Single;

import static com.pushtorefresh.storio3.internal.Environment.throwExceptionIfRxJava2IsNotAvailable;

public class RxJavaUtils {

    private RxJavaUtils() {
        throw new IllegalStateException("No instances please.");
    }

    @CheckResult
    @NonNull
    public static <Result, WrappedResult, Data> Flowable<Result> createFlowable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<Result, WrappedResult, Data> operation,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxFlowable()");

        final Flowable<Result> flowable = Flowable.create(
                new FlowableOnSubscribeExecuteAsBlocking<Result, WrappedResult, Data>(operation), backpressureStrategy
        );

        return subscribeOn(
                storIOContentResolver,
                flowable
        );
    }

    @CheckResult
    @NonNull
    public static <Result, WrappedResult> Flowable<Result> createGetFlowable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<Result, WrappedResult, Query> operation,
            @NonNull Query query,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxFlowable()");

        final Flowable<Result> flowable = storIOContentResolver
                .observeChangesOfUri(query.uri(), backpressureStrategy) // each change triggers executeAsBlocking
                .map(new MapSomethingToExecuteAsBlocking<Changes, Result, WrappedResult, Query>(operation))
                .startWith(Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<Result, WrappedResult, Query>(operation), backpressureStrategy)); // start stream with first query result

        return subscribeOn(
                storIOContentResolver,
                flowable
        );
    }

    @CheckResult
    @NonNull
    public static <Result> Flowable<Optional<Result>> createGetFlowableOptional(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<Result, Optional<Result>, Query> operation,
            @NonNull Query query,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxFlowable()");

        final Flowable<Optional<Result>> flowable = storIOContentResolver
                .observeChangesOfUri(query.uri(), backpressureStrategy) // each change triggers executeAsBlocking
                .map(new MapSomethingToExecuteAsBlockingOptional<Changes, Result, Query>(operation))
                .startWith(Flowable.create(new FlowableOnSubscribeExecuteAsBlockingOptional<Result, Query>(operation), backpressureStrategy)); // start stream with first query result

        return subscribeOn(
                storIOContentResolver,
                flowable
        );
    }

    @CheckResult
    @NonNull
    public static <Result, WrappedResult, Data> Single<Result> createSingle(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<Result, WrappedResult, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxSingle()");

        final Single<Result> single = Single.create(new SingleOnSubscribeExecuteAsBlocking<Result, WrappedResult, Data>(operation));

        return subscribeOn(
                storIOContentResolver,
                single
        );
    }

    @CheckResult
    @NonNull
    public static <Result, Data> Single<Optional<Result>> createSingleOptional(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<Result, Optional<Result>, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxSingle()");

        final Single<Optional<Result>> single = Single.create(new SingleOnSubscribeExecuteAsBlockingOptional<Result, Data>(operation));

        return subscribeOn(
                storIOContentResolver,
                single
        );
    }

    @CheckResult
    @NonNull
    public static <Result, Data> Completable createCompletable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedCompletableOperation<Result, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxCompletable()");

        final Completable completable = Completable.create(new CompletableOnSubscribeExecuteAsBlocking(operation));

        return subscribeOn(
                storIOContentResolver,
                completable
        );
    }

    @CheckResult
    @NonNull
    public static <Result, WrappedResult, Data> Maybe<Result> createMaybe(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedMaybeOperation<Result, WrappedResult, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxMaybe()");

        final Maybe<Result> maybe =
                Maybe.create(new MaybeOnSubscribeExecuteAsBlocking<Result, WrappedResult, Data>(operation));

        return subscribeOn(storIOContentResolver, maybe);
    }

    @CheckResult
    @NonNull
    public static <T> Flowable<T> subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Flowable<T> flowable
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultRxScheduler();
        return scheduler != null ? flowable.subscribeOn(scheduler) : flowable;
    }

    @CheckResult
    @NonNull
    public static <T> Single<T> subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Single<T> single
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultRxScheduler();
        return scheduler != null ? single.subscribeOn(scheduler) : single;
    }

    @CheckResult
    @NonNull
    public static Completable subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Completable completable
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultRxScheduler();
        return scheduler != null ? completable.subscribeOn(scheduler) : completable;
    }

    @CheckResult
    @NonNull
    public static <T> Maybe<T> subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Maybe<T> maybe
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultRxScheduler();
        return scheduler != null ? maybe.subscribeOn(scheduler) : maybe;
    }
}
