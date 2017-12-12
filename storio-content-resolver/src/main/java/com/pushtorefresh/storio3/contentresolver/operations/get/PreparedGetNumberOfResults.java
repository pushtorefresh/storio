package com.pushtorefresh.storio3.contentresolver.operations.get;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static com.pushtorefresh.storio3.internal.Checks.checkNotNull;

public class PreparedGetNumberOfResults extends PreparedGetMandatoryResult<Integer> {

    @NonNull
    private final GetResolver<Integer> getResolver;

    PreparedGetNumberOfResults(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Query query, @NonNull GetResolver<Integer> getResolver) {
        super(storIOContentResolver, query);
        this.getResolver = getResolver;
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
            final Cursor cursor;

            try {
                cursor = getResolver.performGet(storIOContentResolver, query);
                try {
                    //noinspection unchecked
                    return (Result) getResolver.mapFromCursor(storIOContentResolver, cursor);
                } finally {
                    cursor.close();
                }
            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Get operation. query = " + query, exception);
            }
        }
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
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Flowable} because
     * it's "Hot" and endless.
     *
     * @return non-null {@link Flowable} which will emit non-null
     * number of results of the executed query and will be subscribed to changes of tables from query.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<Integer> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createGetFlowable(storIOContentResolver, this, query, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will get number of results lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will get number of results.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<Integer> asRxSingle() {
        return RxJavaUtils.createSingle(storIOContentResolver, this);
    }

    /**
     * Builder for {@link PreparedGetNumberOfResults}.
     */
     public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Required: Specifies query which will be passed to {@link StorIOContentResolver}
         * to get list of objects.
         *
         * @param query non-null query.
         * @return builder.
         * @see Query
         */
        @NonNull
        public CompleteBuilder withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify query");
            return new CompleteBuilder(storIOContentResolver, query);
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
            public Integer mapFromCursor(
                    @NonNull StorIOContentResolver storIOContentResolver,
                    @NonNull Cursor cursor
            ) {
                return cursor.getCount();
            }
        };

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Query query;

        @Nullable
        private GetResolver<Integer> getResolver;

        CompleteBuilder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Query query) {
            this.storIOContentResolver = storIOContentResolver;
            this.query = query;
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

            return new PreparedGetNumberOfResults(
                    storIOContentResolver,
                    query,
                    getResolver
            );
        }
    }

}
