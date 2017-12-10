package com.pushtorefresh.storio3.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static com.pushtorefresh.storio3.internal.Checks.checkNotNull;

/**
 * Prepared Get Operation for {@link StorIOSQLite}.
 */
public class PreparedGetCursor extends PreparedGetMandatoryResult<Cursor> {

    @NonNull
    private final GetResolver<Cursor> getResolver;

    PreparedGetCursor(@NonNull StorIOSQLite storIOSQLite,
                      @NonNull Query query,
                      @NonNull GetResolver<Cursor> getResolver) {
        super(storIOSQLite, query);
        this.getResolver = getResolver;
    }

    PreparedGetCursor(@NonNull StorIOSQLite storIOSQLite,
                      @NonNull RawQuery rawQuery,
                      @NonNull GetResolver<Cursor> getResolver) {
        super(storIOSQLite, rawQuery);
        this.getResolver = getResolver;
    }

    /**
     * Creates "Hot" {@link Flowable} which will be subscribed to changes of tables from query
     * and will emit result each time change occurs.
     * <p>
     * First result will be emitted immediately after subscription,
     * other emissions will occur only if changes of tables from query will occur during lifetime of
     * the {@link Flowable}.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Flowable} because
     * it's "Hot" and endless.
     *
     * @return non-null {@link Flowable} which will emit non-null
     * list with mapped results and will be subscribed to changes of tables from query.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<Cursor> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createGetFlowable(storIOSQLite, this, query, rawQuery, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will perform Get Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Get Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<Cursor> asRxSingle() {
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
        public <Result, WrappedResult, Data> Result intercept(@NonNull PreparedOperation<Result, WrappedResult, Data> operation, @NonNull Chain chain) {
            try {
                if (query != null) {
                    //noinspection unchecked
                    return (Result) getResolver.performGet(storIOSQLite, query);
                } else if (rawQuery != null) {
                    //noinspection unchecked
                    return (Result) getResolver.performGet(storIOSQLite, rawQuery);
                } else {
                    throw new IllegalStateException("Please specify query");
                }
            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Get operation. query = " + (query != null ? query : rawQuery), exception);
            }
        }
    }

    /**
     * Builder for {@link PreparedGetCursor}.
     * <p>
     * Required: You should specify query by call
     * {@link #withQuery(Query)} or {@link #withQuery(RawQuery)}.
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Required: Specifies {@link Query} for Get Operation.
         *
         * @param query query.
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
     * Compile-time safe part of builder for {@link PreparedGetCursor}.
     */
    public static class CompleteBuilder {

        @NonNull
        static final GetResolver<Cursor> STANDARD_GET_RESOLVER = new DefaultGetResolver<Cursor>() {
            @NonNull
            @Override
            public Cursor mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor) {
                return cursor; // no modifications
            }
        };

        @NonNull
        private final StorIOSQLite storIOSQLite;

        Query query;

        RawQuery rawQuery;

        private GetResolver<Cursor> getResolver;

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
         * Optional: Specifies Get Resolver for operation.
         * If no value is set, builder will use resolver that
         * simply redirects query to {@link StorIOSQLite}.
         *
         * @param getResolver nullable GetResolver for Get Operation.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder withGetResolver(@Nullable GetResolver<Cursor> getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Prepares Get Operation.
         *
         * @return {@link PreparedGetCursor} instance.
         */
        @NonNull
        public PreparedGetCursor prepare() {
            if (getResolver == null) {
                getResolver = STANDARD_GET_RESOLVER;
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
}
