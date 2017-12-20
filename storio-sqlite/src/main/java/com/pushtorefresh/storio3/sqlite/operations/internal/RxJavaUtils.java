package com.pushtorefresh.storio3.sqlite.operations.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.operations.PreparedMaybeOperation;
import com.pushtorefresh.storio3.operations.PreparedCompletableOperation;
import com.pushtorefresh.storio3.operations.internal.CompletableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.FlowableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.FlowableOnSubscribeExecuteAsBlockingOptional;
import com.pushtorefresh.storio3.operations.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.MapSomethingToExecuteAsBlockingOptional;
import com.pushtorefresh.storio3.operations.internal.MaybeOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.SingleOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio3.operations.internal.SingleOnSubscribeExecuteAsBlockingOptional;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.impl.ChangesFilter;
import com.pushtorefresh.storio3.sqlite.queries.GetQuery;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;

import io.reactivex.Maybe;
import java.util.Collections;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

import static com.pushtorefresh.storio3.internal.Environment.throwExceptionIfRxJava2IsNotAvailable;

public final class RxJavaUtils {

    private RxJavaUtils() {
        throw new IllegalStateException("No instances please.");
    }

    @CheckResult
    @NonNull
    public static <Result, WrappedResult, Data> Flowable<Result> createFlowable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<Result, WrappedResult, Data> operation,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxFlowable()");

        return subscribeOn(
                storIOSQLite,
                Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<Result, WrappedResult, Data>(operation), backpressureStrategy)
        );
    }

    @CheckResult
    @NonNull
    public static <Result, WrappedResult> Flowable<Result> createGetFlowable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<Result, WrappedResult, GetQuery> operation,
            @Nullable Query query,
            @Nullable RawQuery rawQuery,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxFlowable()");

        final Set<String> tables = extractTables(query, rawQuery);
        final Set<String> tags = extractTags(query, rawQuery);

        final Flowable<Result> flowable;

        if (!tables.isEmpty() || !tags.isEmpty()) {
            flowable = ChangesFilter.applyForTablesAndTags(storIOSQLite.observeChanges(backpressureStrategy), tables, tags)
                    .map(new MapSomethingToExecuteAsBlocking<Changes, Result, WrappedResult, GetQuery>(operation))  // each change triggers executeAsBlocking
                    .startWith(Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<Result, WrappedResult, GetQuery>(operation), backpressureStrategy)); // start stream with first query result
        } else {
            flowable = Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<Result, WrappedResult, GetQuery>(operation), backpressureStrategy);
        }

        return RxJavaUtils.subscribeOn(storIOSQLite, flowable);
    }

    @CheckResult
    @NonNull
    public static <Result> Flowable<Optional<Result>> createGetFlowableOptional(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<Result, Optional<Result>, GetQuery> operation,
            @Nullable Query query,
            @Nullable RawQuery rawQuery,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxFlowable()");

        final Set<String> tables = extractTables(query, rawQuery);
        final Set<String> tags = extractTags(query, rawQuery);

        final Flowable<Optional<Result>> flowable;

        if (!tables.isEmpty() || !tags.isEmpty()) {
            flowable = ChangesFilter.applyForTablesAndTags(storIOSQLite.observeChanges(backpressureStrategy), tables, tags)
                    .map(new MapSomethingToExecuteAsBlockingOptional<Changes, Result, GetQuery>(operation))  // each change triggers executeAsBlocking
                    .startWith(Flowable.create(new FlowableOnSubscribeExecuteAsBlockingOptional<Result, GetQuery>(operation), backpressureStrategy)); // start stream with first query result
        } else {
            flowable = Flowable.create(new FlowableOnSubscribeExecuteAsBlockingOptional<Result, GetQuery>(operation), backpressureStrategy);
        }

        return RxJavaUtils.subscribeOn(storIOSQLite, flowable);
    }

    @CheckResult
    @NonNull
    public static <Result, WrappedResult, Data> Single<Result> createSingle(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<Result, WrappedResult, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxSingle()");

        final Single<Result> single =
                Single.create(new SingleOnSubscribeExecuteAsBlocking<Result, WrappedResult, Data>(operation));

        return subscribeOn(storIOSQLite, single);
    }

    @CheckResult
    @NonNull
    public static <Result, Data> Single<Optional<Result>> createSingleOptional(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<Result, Optional<Result>, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxSingle()");

        final Single<Optional<Result>> single =
                Single.create(new SingleOnSubscribeExecuteAsBlockingOptional<Result, Data>(operation));

        return subscribeOn(storIOSQLite, single);
    }

    @CheckResult
    @NonNull
    public static <T, Data> Completable createCompletable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedCompletableOperation<T, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxCompletable()");

        final Completable completable =
                Completable.create(new CompletableOnSubscribeExecuteAsBlocking(operation));

        return subscribeOn(storIOSQLite, completable);
    }

    @CheckResult
    @NonNull
    public static <Result, WrappedResult, Data> Maybe<Result> createMaybe(
        @NonNull StorIOSQLite storIOSQLite,
        @NonNull PreparedMaybeOperation<Result, WrappedResult, Data> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxMaybe()");

        final Maybe<Result> maybe =
            Maybe.create(new MaybeOnSubscribeExecuteAsBlocking<Result, WrappedResult, Data>(operation));

        return subscribeOn(storIOSQLite, maybe);
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

    @CheckResult
    @NonNull
    public static <T> Maybe<T> subscribeOn(
        @NonNull StorIOSQLite storIOSQLite,
        @NonNull Maybe<T> maybe
    ) {
        final Scheduler scheduler = storIOSQLite.defaultRxScheduler();
        return scheduler != null ? maybe.subscribeOn(scheduler) : maybe;
    }

    @VisibleForTesting
    @NonNull
    static Set<String> extractTables(
            @Nullable Query query,
            @Nullable RawQuery rawQuery
    ) {
        if (query != null) {
            return Collections.singleton(query.table());
        } else if (rawQuery != null) {
            return rawQuery.observesTables();
        } else {
            throw new IllegalStateException("Please specify query");
        }
    }

    @VisibleForTesting
    @NonNull
    static Set<String> extractTags(
            @Nullable Query query,
            @Nullable RawQuery rawQuery
    ) {
        if (query != null) {
            return query.observesTags();
        } else if (rawQuery != null) {
            return rawQuery.observesTags();
        } else {
            throw new IllegalStateException("Please specify query");
        }
    }
}
