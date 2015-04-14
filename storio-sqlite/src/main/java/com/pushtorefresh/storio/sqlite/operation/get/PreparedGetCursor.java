package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.operation.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import java.util.HashSet;
import java.util.Set;

import rx.Observable;

public class PreparedGetCursor extends PreparedGet<Cursor> {

    PreparedGetCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        super(storIOSQLite, query);
    }

    PreparedGetCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        super(storIOSQLite, rawQuery);
    }

    /**
     * Executes Get Operation immediately in current thread
     *
     * @return non-null {@link Cursor}, can be empty
     */
    @NonNull
    public Cursor executeAsBlocking() {
        if (query != null) {
            return storIOSQLite.internal().query(query);
        } else if (rawQuery != null) {
            return storIOSQLite.internal().rawQuery(rawQuery);
        } else {
            throw new IllegalStateException("Please specify query");
        }
    }

    /**
     * Creates an {@link Observable} which will emit result of operation
     *
     * @return non-null {@link Observable} which will emit non-null {@link Cursor}, can be empty
     */
    @NonNull
    @Override
    public Observable<Cursor> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Creates an {@link Observable} which will be subscribed to changes of query tables
     * and will emit result each time change occurs
     * <p>
     * First result will be emitted immediately after subscription,
     * other emissions will occur only if changes of query tables will occur
     *
     * @return non-null {@link Observable} which will emit {@link Cursor} and will be subscribed to changes of query tables
     */
    @NonNull
    @Override
    public Observable<Cursor> createObservableStream() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservableStream()");

        final Set<String> tables;

        if (query != null) {
            tables = new HashSet<String>(1);
            tables.add(query.table);
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
     * Builder for {@link PreparedOperationWithReactiveStream}
     * <p>
     * Required: You should specify query by call
     * {@link #withQuery(Query)} or {@link #withQuery(RawQuery)}
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Specifies {@link Query} for Get Operation
         *
         * @param query query
         * @return builder
         */
        @NonNull
        public CompleteBuilder withQuery(@NonNull Query query) {
            return new CompleteBuilder(storIOSQLite, query);
        }

        /**
         * Specifies {@link RawQuery} for Get Operation,
         * you can use it for "joins" and same constructions which are not allowed in {@link Query}
         *
         * @param rawQuery query
         * @return builder
         */
        @NonNull
        public CompleteBuilder withQuery(@NonNull RawQuery rawQuery) {
            return new CompleteBuilder(storIOSQLite, rawQuery);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedOperationWithReactiveStream}
     */
    public static class CompleteBuilder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        private final Query query;
        private final RawQuery rawQuery;

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
            this.storIOSQLite = storIOSQLite;
            this.query = query;
            this.rawQuery = null;
        }

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
            this.storIOSQLite = storIOSQLite;
            this.rawQuery = rawQuery;
            this.query = null;
        }

        /**
         * Prepares Get Operation
         *
         * @return {@link PreparedGetCursor} instance
         */
        @NonNull
        public PreparedOperationWithReactiveStream<Cursor> prepare() {
            if (query != null) {
                return new PreparedGetCursor(storIOSQLite, query);
            } else if (rawQuery != null) {
                return new PreparedGetCursor(storIOSQLite, rawQuery);
            } else {
                throw new IllegalStateException("Please specify query");
            }
        }
    }
}
