package com.pushtorefresh.storio2.sqlite.operations.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.operations.internal.CompletableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.operations.internal.FlowableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.operations.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio2.operations.internal.SingleOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.impl.ChangesFilter;
import com.pushtorefresh.storio2.sqlite.queries.GetQuery;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

import java.util.Collections;
import java.util.Set;

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
    public static <T> Flowable<T> createGetFlowable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PreparedOperation<T, GetQuery> operation,
            @Nullable Query query,
            @Nullable RawQuery rawQuery,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxFlowable()");

        final Set<String> tables;
        final Set<String> tags;

        if (query != null) {
            tables = Collections.singleton(query.table());
            tags = query.observesTags();
        } else if (rawQuery != null) {
            tables = rawQuery.observesTables();
            tags = rawQuery.observesTags();
        } else {
            throw new IllegalStateException("Please specify query");
        }

        final Flowable<T> flowable;

        if (!tables.isEmpty() || !tags.isEmpty()) {
            flowable = ChangesFilter.applyForTablesAndTags(storIOSQLite.observeChanges(backpressureStrategy), tables, tags)
                    .map(new MapSomethingToExecuteAsBlocking<Changes, T, GetQuery>(operation))  // each change triggers executeAsBlocking
                    .startWith(Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<T, GetQuery>(operation), backpressureStrategy)); // start stream with first query result
        } else {
            flowable = Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<T, GetQuery>(operation), backpressureStrategy);
        }

        return RxJavaUtils.subscribeOn(storIOSQLite, flowable);
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
