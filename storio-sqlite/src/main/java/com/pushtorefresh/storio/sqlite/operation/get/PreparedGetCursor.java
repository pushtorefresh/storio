package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class PreparedGetCursor extends PreparedGet<Cursor> {

    PreparedGetCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query, @NonNull GetResolver getResolver) {
        super(storIOSQLite, query, getResolver);
    }

    PreparedGetCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery, @NonNull GetResolver getResolver) {
        super(storIOSQLite, rawQuery, getResolver);
    }

    @NonNull public Cursor executeAsBlocking() {
        if (query != null) {
            return getResolver.performGet(storIOSQLite, query);
        } else if (rawQuery != null) {
            return getResolver.performGet(storIOSQLite, rawQuery);
        } else {
            throw new IllegalStateException("Please specify query");
        }
    }

    @NonNull @Override public Observable<Cursor> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override public void call(Subscriber<? super Cursor> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }

    @NonNull @Override public Observable<Cursor> createObservableStream() {
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
                    .observeChangesInTables(tables)
                    .map(new Func1<Changes, Cursor>() { // each change triggers executeAsBlocking
                        @Override public Cursor call(Changes changes) {
                            return executeAsBlocking();
                        }
                    })
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
     * Builder for {@link PreparedOperationWithReactiveStream}
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
