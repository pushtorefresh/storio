package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.operations.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.ChangesFilter;
import com.pushtorefresh.storio.sqlite.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Single;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.unmodifiableList;

/**
 * Prepared Get Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of results.
 */
public class PreparedGetListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull
    private final Class<T> type;

    @Nullable
    private final GetResolver<T> explicitGetResolver;

    PreparedGetListOfObjects(@NonNull StorIOSQLite storIOSQLite,
                             @NonNull Class<T> type,
                             @NonNull Query query,
                             @Nullable GetResolver<T> explicitGetResolver) {
        super(storIOSQLite, query);
        this.type = type;
        this.explicitGetResolver = explicitGetResolver;
    }

    PreparedGetListOfObjects(@NonNull StorIOSQLite storIOSQLite,
                             @NonNull Class<T> type,
                             @NonNull RawQuery rawQuery,
                             @Nullable GetResolver<T> explicitGetResolver) {
        super(storIOSQLite, rawQuery);
        this.type = type;
        this.explicitGetResolver = explicitGetResolver;
    }

    /**
     * Executes Get Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null, immutable {@link List} with mapped results, list can be empty.
     */
    @WorkerThread
    @SuppressWarnings({"TryFinallyCanBeTryWithResources", "unchecked"})
    // Min SDK :( unchecked for empty list
    @NonNull
    @Override
    public List<T> executeAsBlocking() {
        try {
            final GetResolver<T> getResolver;

            if (explicitGetResolver != null) {
                getResolver = explicitGetResolver;
            } else {
                final SQLiteTypeMapping<T> typeMapping = storIOSQLite.lowLevel().typeMapping(type);

                if (typeMapping == null) {
                    throw new IllegalStateException("This type does not have type mapping: " +
                            "type = " + type + "," +
                            "db was not touched by this operation, please add type mapping for this type");
                }

                getResolver = typeMapping.getResolver();
            }

            final Cursor cursor;

            if (query != null) {
                cursor = getResolver.performGet(storIOSQLite, query);
            } else if (rawQuery != null) {
                cursor = getResolver.performGet(storIOSQLite, rawQuery);
            } else {
                throw new IllegalStateException("Please specify query");
            }

            try {
                final int count = cursor.getCount();

                if (count == 0) {
                    return EMPTY_LIST; // it's immutable
                }

                final List<T> list = new ArrayList<T>(count);

                while (cursor.moveToNext()) {
                    list.add(getResolver.mapFromCursor(cursor));
                }

                return unmodifiableList(list);
            } finally {
                cursor.close();
            }
        } catch (Exception exception) {
            throw new StorIOException("Error has occurred during Get operation. query = " + (query != null ? query : rawQuery), exception);
        }
    }

    /**
     * Creates "Hot" {@link Observable} which will be subscribed to changes of tables from query
     * and will emit result each time change occurs.
     * <p>
     * First result will be emitted immediately after subscription,
     * other emissions will occur only if changes of tables from query will occur during lifetime of
     * the {@link Observable}.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Observable} because
     * it's "Hot" and endless.
     *
     * @return non-null {@link Observable} which will emit non-null, immutable
     * {@link List} with mapped results and will be subscribed to changes of tables from query,
     * list can be empty.
     * @deprecated (will be removed in 2.0) please use {@link #asRxObservable()}.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<List<T>> createObservable() {
        return asRxObservable();
    }

    /**
     * Creates "Hot" {@link Observable} which will be subscribed to changes of tables from query
     * and will emit result each time change occurs.
     * <p>
     * First result will be emitted immediately after subscription,
     * other emissions will occur only if changes of tables from query will occur during lifetime of
     * the {@link Observable}.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Observable} because
     * it's "Hot" and endless.
     *
     * @return non-null {@link Observable} which will emit non-null, immutable
     * {@link List} with mapped results and will be subscribed to changes of tables from query,
     * list can be empty.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<List<T>> asRxObservable() {
        throwExceptionIfRxJavaIsNotAvailable("asRxObservable()");

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

        final Observable<List<T>> observable;
        if (!tables.isEmpty() || !tags.isEmpty()) {
            observable = ChangesFilter.applyForTablesAndTags(storIOSQLite.observeChanges(), tables, tags)
                    .map(MapSomethingToExecuteAsBlocking.newInstance(this))  // each change triggers executeAsBlocking
                    .startWith(Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this))) // start stream with first query result
                    .onBackpressureLatest();
        } else {
            observable = Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
        }

        return RxJavaUtils.subscribeOn(storIOSQLite, observable);
    }

    /**
     * Creates {@link Single} which will perform Get Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Get Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<List<T>> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
    }

    /**
     * Builder for {@link PreparedGetListOfObjects} Operation.
     *
     * @param <T> type of objects.
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Class<T> type;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull Class<T> type) {
            this.storIOSQLite = storIOSQLite;
            this.type = type;
        }

        /**
         * Required: Specifies query which will be passed to {@link StorIOSQLite}
         * to get list of objects.
         *
         * @param query non-null query.
         * @return builder.
         * @see Query
         */
        @NonNull
        public CompleteBuilder<T> withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify query");
            return new CompleteBuilder<T>(storIOSQLite, type, query);
        }

        /**
         * Required: Specifies {@link RawQuery} for Get Operation,
         * you can use it for "joins" and same constructions which are not allowed for {@link Query}.
         *
         * @param rawQuery query.
         * @return builder.
         * @see RawQuery
         */
        @NonNull
        public CompleteBuilder<T> withQuery(@NonNull RawQuery rawQuery) {
            checkNotNull(rawQuery, "Please specify rawQuery");
            return new CompleteBuilder<T>(storIOSQLite, type, rawQuery);
        }
    }

    /**
     * Compile-safe part of {@link Builder}.
     *
     * @param <T> type of objects.
     */
    public static class CompleteBuilder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Class<T> type;

        @Nullable
        Query query;

        @Nullable
        RawQuery rawQuery;

        @Nullable
        private GetResolver<T> getResolver;

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull Class<T> type, @NonNull Query query) {
            this.storIOSQLite = storIOSQLite;
            this.type = type;
            this.query = query;
            rawQuery = null;
        }

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull Class<T> type, @NonNull RawQuery rawQuery) {
            this.storIOSQLite = storIOSQLite;
            this.type = type;
            this.rawQuery = rawQuery;
            query = null;
        }

        /**
         * Optional: Specifies resolver for Get Operation which can be used
         * to provide custom behavior of Get Operation.
         * <p>
         * {@link SQLiteTypeMapping} can be used to set default GetResolver.
         * If GetResolver is not set via {@link SQLiteTypeMapping}
         * or explicitly — exception will be thrown.
         *
         * @param getResolver nullable resolver for Get Operation.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder<T> withGetResolver(@Nullable GetResolver<T> getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Builds new instance of {@link PreparedGetListOfObjects}.
         *
         * @return new instance of {@link PreparedGetListOfObjects}.
         */
        @NonNull
        public PreparedGetListOfObjects<T> prepare() {
            if (query != null) {
                return new PreparedGetListOfObjects<T>(
                        storIOSQLite,
                        type,
                        query,
                        getResolver
                );
            } else if (rawQuery != null) {
                return new PreparedGetListOfObjects<T>(
                        storIOSQLite,
                        type,
                        rawQuery,
                        getResolver
                );
            } else {
                throw new IllegalStateException("Please specify Query or RawQuery");
            }
        }
    }
}