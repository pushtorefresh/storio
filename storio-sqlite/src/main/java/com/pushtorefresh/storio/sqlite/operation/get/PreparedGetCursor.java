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

    PreparedGetCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query, @NonNull GetResolver getResolver) {
        super(storIOSQLite, query, getResolver);
    }

    PreparedGetCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery, @NonNull GetResolver getResolver) {
        super(storIOSQLite, rawQuery, getResolver);
    }

    /**
     * Executes Get Operation immediately in current thread
     *
     * @return non-null {@link Cursor}, can be empty
     */
    @NonNull
    public Cursor executeAsBlocking() {
        if (query != null) {
            return getResolver.performGet(storIOSQLite, query);
        } else if (rawQuery != null) {
            return getResolver.performGet(storIOSQLite, rawQuery);
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
     * <p/>
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

    interface CommonBuilder<T> {

        /**
         * Optional: Specifies {@link GetResolver} for Get Operation
         * which allows you to customize behavior of Get Operation
         * <p>
         * Default value is instance of {@link DefaultGetResolver}
         *
         * @param getResolver get resolver
         * @return builder
         */
        @NonNull
        T withGetResolver(@NonNull GetResolver getResolver);
    }

    /**
     * Builder for {@link PreparedOperationWithReactiveStream}
     * <p>
     * Required: You should specify query by call
     * {@link #withQuery(Query)} or {@link #withQuery(RawQuery)}
     */
    public static class Builder implements CommonBuilder<Builder> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        private Query query;
        private RawQuery rawQuery;
        private GetResolver getResolver;

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
            this.query = query;
            return new CompleteBuilder(this);
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
            this.rawQuery = rawQuery;
            return new CompleteBuilder(this);
        }

        @Override
        @NonNull
        public Builder withGetResolver(@NonNull GetResolver getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Hidden method for prepares Get Operation
         *
         * @return {@link PreparedGetCursor} instance
         */
        @NonNull
        private PreparedOperationWithReactiveStream<Cursor> prepare() {
            if (getResolver == null) {
                getResolver = DefaultGetResolver.INSTANCE;
            }

            if (query != null) {
                return new PreparedGetCursor(storIOSQLite, query, getResolver);
            } else if (rawQuery != null) {
                return new PreparedGetCursor(storIOSQLite, rawQuery, getResolver);
            } else {
                throw new IllegalStateException("Please specify query");
            }
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedOperationWithReactiveStream}
     */
    public static class CompleteBuilder implements CommonBuilder<CompleteBuilder> {

        private final Builder incompleteBuilder;

        CompleteBuilder(@NonNull Builder builder) {
            this.incompleteBuilder = builder;
        }

        @Override
        @NonNull
        public CompleteBuilder withGetResolver(@NonNull GetResolver getResolver) {
            incompleteBuilder.withGetResolver(getResolver);
            return this;
        }

        /**
         * Prepares Get Operation
         *
         * @return {@link PreparedGetCursor} instance
         */
        @NonNull
        public PreparedOperationWithReactiveStream<Cursor> prepare() {
            return incompleteBuilder.prepare();
        }
    }
}
