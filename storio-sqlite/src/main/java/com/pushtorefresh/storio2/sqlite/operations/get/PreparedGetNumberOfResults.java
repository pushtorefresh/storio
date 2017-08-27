package com.pushtorefresh.storio2.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.operations.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio2.operations.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.sqlite.Interceptor;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.impl.ChangesFilter;
import com.pushtorefresh.storio2.sqlite.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

import java.util.Collections;
import java.util.Set;

import rx.Observable;
import rx.Single;

import static com.pushtorefresh.storio2.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio2.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

public class PreparedGetNumberOfResults extends PreparedGet<Integer> {

    @NonNull
    private final GetResolver<Integer> getResolver;

    PreparedGetNumberOfResults(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query, @NonNull GetResolver<Integer> getResolver) {
        super(storIOSQLite, query);
        this.getResolver = getResolver;
    }

    PreparedGetNumberOfResults(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery, @NonNull GetResolver<Integer> getResolver) {
        super(storIOSQLite, rawQuery);
        this.getResolver = getResolver;
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
     * @return non-null {@link Observable} which will emit non-null
     * number of results of the executed query and will be subscribed to changes of tables from query.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<Integer> asRxObservable() {
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
            throw new StorIOException("Please specify query");
        }

        final Observable<Integer> observable;
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
     * Creates {@link Single} which will get number of results lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will get number of results.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<Integer> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
    }

    @NonNull
    @Override
    protected Interceptor getRealCallInterceptor() {
        return new RealCallInterceptor();
    }

    private class RealCallInterceptor implements Interceptor {
        @NonNull
        @Override
        public <Result, Data> Result intercept(@NonNull PreparedOperation<Result, Data> operation, @NonNull Chain chain) {
            final Cursor cursor;

            try {
                if (query != null) {
                    cursor = getResolver.performGet(storIOSQLite, query);
                } else if (rawQuery != null) {
                    cursor = getResolver.performGet(storIOSQLite, rawQuery);
                } else {
                    throw new IllegalStateException("Please specify query");
                }

                try {
                    //noinspection unchecked
                    return (Result) getResolver.mapFromCursor(storIOSQLite, cursor);
                } finally {
                    cursor.close();
                }
            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Get operation. query = " + (query != null ? query : rawQuery), exception);
            }
        }
    }

    /**
     * Builder for {@link PreparedGetNumberOfResults}.
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
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
        public CompleteBuilder withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify query");
            return new CompleteBuilder(storIOSQLite, query);
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
        public CompleteBuilder withQuery(@NonNull RawQuery rawQuery) {
            checkNotNull(rawQuery, "Please specify rawQuery");
            return new CompleteBuilder(storIOSQLite, rawQuery);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedGetNumberOfResults}.
     */
    public static class CompleteBuilder {

        @NonNull
        static final GetResolver<Integer> STANDARD_GET_RESOLVER = new DefaultGetResolver<Integer>() {
            @NonNull
            @Override
            public Integer mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor) {
                return cursor.getCount();
            }
        };

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @Nullable
        Query query;

        @Nullable
        RawQuery rawQuery;

        @Nullable
        private GetResolver<Integer> getResolver;

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
            this.storIOSQLite = storIOSQLite;
            this.query = query;
            rawQuery = null;
        }

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
            this.storIOSQLite = storIOSQLite;
            this.rawQuery = rawQuery;
            query = null;
        }

        /**
         * Optional: Specifies resolver for Get Operation which can be used
         * to provide custom behavior of Get Operation.
         * <p>
         *
         * @param getResolver nullable resolver for Get Operation.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder withGetResolver(@Nullable GetResolver<Integer> getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Builds new instance of {@link PreparedGetNumberOfResults}.
         *
         * @return new instance of {@link PreparedGetNumberOfResults}.
         */
        @NonNull
        public PreparedGetNumberOfResults prepare() {
            if (getResolver == null) {
                getResolver = STANDARD_GET_RESOLVER;
            }

            if (query != null) {
                return new PreparedGetNumberOfResults(
                        storIOSQLite,
                        query,
                        getResolver
                );
            } else if (rawQuery != null) {
                return new PreparedGetNumberOfResults(
                        storIOSQLite,
                        rawQuery,
                        getResolver
                );
            } else {
                throw new IllegalStateException("Please specify query");
            }
        }
    }

}
