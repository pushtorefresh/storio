package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Get Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of results.
 */
public final class PreparedGetListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull
    private final GetResolver<T> getResolver;

    PreparedGetListOfObjects(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query, @NonNull GetResolver<T> getResolver) {
        super(storIOSQLite, query);
        this.getResolver = getResolver;
    }

    PreparedGetListOfObjects(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery, @NonNull GetResolver<T> getResolver) {
        super(storIOSQLite, rawQuery);
        this.getResolver = getResolver;
    }

    /**
     * Executes Get Operation immediately in current thread.
     *
     * @return non-null {@link List} with mapped results, can be empty.
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
    @NonNull
    public List<T> executeAsBlocking() {
        final Cursor cursor;

        if (query != null) {
            cursor = getResolver.performGet(storIOSQLite, query);
        } else if (rawQuery != null) {
            cursor = getResolver.performGet(storIOSQLite, rawQuery);
        } else {
            throw new IllegalStateException("Please specify query");
        }

        try {
            final List<T> list = new ArrayList<T>(cursor.getCount());

            while (cursor.moveToNext()) {
                list.add(getResolver.mapFromCursor(cursor));
            }

            return list;
        } finally {
            cursor.close();
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
     * <dd>Does not operate by default on a particular {@link rx.Scheduler}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Observable} because
     * it's "Hot" and endless.
     *
     * @return non-null {@link Observable} which will emit non-null
     * list with mapped results and will be subscribed to changes of tables from query.
     */
    @NonNull
    @Override
    public Observable<List<T>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        final Set<String> tables;

        if (query != null) {
            tables = Collections.singleton(query.table());
        } else if (rawQuery != null) {
            tables = rawQuery.affectedTables();
        } else {
            throw new IllegalStateException("Please specify query");
        }

        if (tables != null && !tables.isEmpty()) {
            return storIOSQLite
                    .observeChangesInTables(tables) // each change triggers executeAsBlocking
                    .map(MapSomethingToExecuteAsBlocking.newInstance(this))
                    .startWith(executeAsBlocking()); // start stream with first query result
        } else {
            return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
        }
    }

    /**
     * Builder for {@link PreparedGetListOfObjects} Operation.
     *
     * @param <T> type of objects.
     */
    public static final class Builder<T> {

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
    public static final class CompleteBuilder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Class<T> type;

        @Nullable
        private final Query query;

        @Nullable
        private final RawQuery rawQuery;

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
         * or explicitly -> exception will be thrown.
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
            final SQLiteTypeMapping<T> typeMapping = storIOSQLite.internal().typeMapping(type);

            if (getResolver == null && typeMapping != null) {
                getResolver = typeMapping.getResolver();
            }

            checkNotNull(getResolver, "Please specify GetResolver");

            if (query != null) {
                return new PreparedGetListOfObjects<T>(
                        storIOSQLite,
                        query,
                        getResolver
                );
            } else if (rawQuery != null) {
                return new PreparedGetListOfObjects<T>(
                        storIOSQLite,
                        rawQuery,
                        getResolver
                );
            } else {
                throw new IllegalStateException("Please specify Query or RawQuery");
            }
        }
    }
}