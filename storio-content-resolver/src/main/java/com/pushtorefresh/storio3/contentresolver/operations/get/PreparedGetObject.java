package com.pushtorefresh.storio3.contentresolver.operations.get;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import com.pushtorefresh.storio3.operations.PreparedMaybeOperation;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static com.pushtorefresh.storio3.internal.Checks.checkNotNull;

/**
 * Represents Get Operation for {@link StorIOContentResolver}
 * which performs query that retrieves single object
 * from {@link android.content.ContentProvider}.
 *
 * @param <T> type of result.
 */
public class PreparedGetObject<T> extends PreparedGet<T, Optional<T>> implements
        PreparedMaybeOperation<T, Optional<T>, Query> {

    @NonNull
    private final Class<T> type;

    @Nullable
    private final GetResolver<T> explicitGetResolver;

    PreparedGetObject(@NonNull StorIOContentResolver storIOContentResolver,
                      @NonNull Class<T> type,
                      @NonNull Query query,
                      @Nullable GetResolver<T> explicitGetResolver) {
        super(storIOContentResolver, query);
        this.type = type;
        this.explicitGetResolver = explicitGetResolver;
    }

    @NonNull
    @Override
    protected Interceptor getRealCallInterceptor() {
        return new RealCallInterceptor();
    }

    private class RealCallInterceptor implements Interceptor {
        @Nullable
        @SuppressWarnings("ConstantConditions")
        @Override
        public <Result, WrappedResult, Data> Result intercept(@NonNull PreparedOperation<Result, WrappedResult, Data> operation, @NonNull Chain chain) {
            try {
                final GetResolver<T> getResolver;

                if (explicitGetResolver != null) {
                    getResolver = explicitGetResolver;
                } else {
                    final ContentResolverTypeMapping<T> typeMapping = storIOContentResolver.lowLevel().typeMapping(type);

                    if (typeMapping == null) {
                        throw new IllegalStateException("This type does not have type mapping: " +
                                "type = " + type + "," +
                                "ContentProvider was not touched by this operation, please add type mapping for this type");
                    }

                    getResolver = typeMapping.getResolver();
                }

                final Cursor cursor = getResolver.performGet(storIOContentResolver, query);

                try {
                    final int count = cursor.getCount();

                    if (count == 0) {
                        return null;
                    } else {
                        cursor.moveToFirst();
                        //noinspection unchecked
                        return (Result) getResolver.mapFromCursor(storIOContentResolver, cursor);
                    }

                } finally {
                    cursor.close();
                }
            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Get operation. query = " + query, exception);
            }
        }
    }

    /**
     * Creates "Hot" {@link Flowable} which will be subscribed to changes of {@link #query} Uri
     * and will emit result each time change occurs.
     * <p>
     * First result will be emitted immediately after subscription,
     * other emissions will occur only if changes of {@link #query} Uri will occur.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Flowable}
     * because it's "Hot" and endless.
     *
     * @return non-null {@link Flowable} which will emit single object
     * (can be {@code null}, if no items are found)
     * with mapped results and will be subscribed to changes of tables from query
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<Optional<T>> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createGetFlowableOptional(storIOContentResolver, this, query, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will perform Get Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Get Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<Optional<T>> asRxSingle() {
        return RxJavaUtils.createSingleOptional(storIOContentResolver, this);
    }

    @NonNull
    @Override
    public Maybe<T> asRxMaybe() {
        return RxJavaUtils.createMaybe(storIOContentResolver, this);
    }

    /**
     * Builder for {@link PreparedGetObject}.
     *
     * @param <T> type of objects for query.
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Class<T> type;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
        }

        /**
         * Required: Specifies {@link Query} for Get Operation.
         *
         * @param query query.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder<T> withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify query");
            return new CompleteBuilder<T>(storIOContentResolver, type, query);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedGetObject}.
     *
     * @param <T> type of objects for query.
     */
    public static class CompleteBuilder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Class<T> type;

        @NonNull
        private final Query query;

        @Nullable
        private GetResolver<T> getResolver;

        CompleteBuilder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type, @NonNull Query query) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
            this.query = query;
        }

        /**
         * Optional: Specifies {@link GetResolver} for Get Operation
         * which allows you to customize behavior of Get Operation.
         * <p>
         * Can be set via {@link ContentResolverTypeMapping},
         * If value is not set via {@link ContentResolverTypeMapping} — exception will be thrown.
         *
         * @param getResolver GetResolver.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder<T> withGetResolver(@Nullable GetResolver<T> getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Builds new instance of {@link PreparedGetObject}.
         *
         * @return new instance of {@link PreparedGetObject}.
         */
        @NonNull
        public PreparedGetObject<T> prepare() {
            return new PreparedGetObject<T>(
                    storIOContentResolver,
                    type,
                    query,
                    getResolver
            );
        }
    }
}
