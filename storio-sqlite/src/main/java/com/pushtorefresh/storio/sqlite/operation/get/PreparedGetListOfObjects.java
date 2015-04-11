package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.operation.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Represents an Operation for {@link StorIOSQLite} which performs query that retrieves data as list of objects
 * from {@link StorIOSQLite}
 *
 * @param <T> type of result
 */
public class PreparedGetListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull
    private final MapFunc<Cursor, T> mapFunc;

    PreparedGetListOfObjects(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query, @NonNull GetResolver getResolver, @NonNull MapFunc<Cursor, T> mapFunc) {
        super(storIOSQLite, query, getResolver);
        this.mapFunc = mapFunc;
    }

    PreparedGetListOfObjects(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery, @NonNull GetResolver getResolver, @NonNull MapFunc<Cursor, T> mapFunc) {
        super(storIOSQLite, rawQuery, getResolver);
        this.mapFunc = mapFunc;
    }

    /**
     * Executes Prepared Operation immediately in current thread
     *
     * @return non-null list with mapped results, can be empty
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
                list.add(mapFunc.map(cursor));
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    /**
     * Creates an {@link Observable} which will emit result of operation
     *
     * @return non-null {@link Observable} which will emit non-null list with mapped results, list can be empty
     */
    @NonNull
    public Observable<List<T>> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Creates an {@link Observable} which will be subscribed to changes of query tables
     * and will emit result each time change occurs
     * <p/>
     * First result will be emitted immediately,
     * other emissions will occur only if changes of query tables will occur
     *
     * @return non-null {@link Observable} which will emit non-null list with mapped results and will be subscribed to changes of query tables
     */
    @NonNull
    @Override
    public Observable<List<T>> createObservableStream() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservableStream()");

        final Set<String> tables;

        if (query != null) {
            tables = Collections.singleton(query.table);
        } else if (rawQuery != null) {
            tables = rawQuery.affectedTables;
        } else {
            throw new IllegalStateException("Please specify query");
        }

        if (tables != null && !tables.isEmpty()) {
            return storIOSQLite
                    .observeChangesInTables(tables) // each change triggers executeAsBlocking
                    .map(MapSomethingToExecuteAsBlocking.newInstance(this))
                    .startWith(executeAsBlocking()); // start stream with first query result
        } else {
            return createObservable();
        }
    }

    /**
     * Builder for {@link PreparedGetListOfObjects} Operation
     *
     * @param <T> type of objects
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
         * Required: Specifies query which will be passed to {@link StorIOSQLite} to get list of objects
         *
         * @param query non-null query
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify query");
            return new CompleteBuilder<T>(storIOSQLite, type, query);
        }

        /**
         * Required: Specifies query which will be passed to {@link StorIOSQLite} to get list of objects
         *
         * @param rawQuery non-null query
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withQuery(@NonNull RawQuery rawQuery) {
            checkNotNull(rawQuery, "Please specify query");
            return new CompleteBuilder<T>(storIOSQLite, type, rawQuery);
        }
    }

    /**
     * Compile-safe part of {@link Builder}
     *
     * @param <T> type of objects
     */
    public static class CompleteBuilder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Class<T> type;

        @Nullable
        private final Query query;

        @Nullable
        private final RawQuery rawQuery;

        private MapFunc<Cursor, T> mapFunc;
        private GetResolver getResolver;

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
         * Optional: Specifies map function that will be used to map each value from {@link Cursor} to object of required type
         * <p/>
         * {@link SQLiteTypeDefaults} can be used to set default map function,
         * if map function is not set via {@link SQLiteTypeDefaults} or explicitly, exception will be thrown
         *
         * @param mapFunc nullable map function that will be used to map each value from {@link Cursor} to object of required type
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withMapFunc(@Nullable MapFunc<Cursor, T> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        /**
         * Optional: Specifies resolver for Get Operation which can be used to provide custom behavior of Get Operation
         * <p/>
         * {@link SQLiteTypeDefaults} can be used to set default GetResolver
         * If GetResolver is not set via {@link SQLiteTypeDefaults} or explicitly, instance of {@link DefaultGetResolver} will be used
         *
         * @param getResolver nullable resolver for Get Operation
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withGetResolver(@Nullable GetResolver getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Builds new instance of {@link PreparedGetListOfObjects}
         *
         * @return new instance of {@link PreparedGetListOfObjects}
         */
        @NonNull
        public PreparedGetListOfObjects<T> prepare() {
            final SQLiteTypeDefaults<T> typeDefaults = storIOSQLite.internal().typeDefaults(type);

            if (mapFunc == null && typeDefaults != null) {
                mapFunc = typeDefaults.mapFromCursor;
            }

            checkNotNull(mapFunc, "Please specify map function");

            if (getResolver == null) {
                if (typeDefaults != null && typeDefaults.getResolver != null) {
                    getResolver = typeDefaults.getResolver;
                } else {
                    getResolver = DefaultGetResolver.INSTANCE;
                }
            }

            checkNotNull(getResolver, "Please specify Get Resolver");

            if (query != null) {
                return new PreparedGetListOfObjects<T>(
                        storIOSQLite,
                        query,
                        getResolver,
                        mapFunc
                );
            } else if (rawQuery != null) {
                return new PreparedGetListOfObjects<T>(
                        storIOSQLite,
                        rawQuery,
                        getResolver,
                        mapFunc
                );
            } else {
                throw new IllegalStateException("Please specify Query or RawQuery");
            }
        }
    }
}