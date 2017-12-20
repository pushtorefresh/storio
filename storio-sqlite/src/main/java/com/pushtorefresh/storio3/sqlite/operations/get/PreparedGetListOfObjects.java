package com.pushtorefresh.storio3.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static com.pushtorefresh.storio3.internal.Checks.checkNotNull;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.unmodifiableList;

/**
 * Prepared Get Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of results.
 */
public class PreparedGetListOfObjects<T> extends PreparedGetMandatoryResult<List<T>> {

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
     * Please don't forget to dispose from this {@link Flowable} because
     * it's "Hot" and endless.
     *
     * @return non-null {@link Flowable} which will emit non-null, immutable
     * {@link List} with mapped results and will be subscribed to changes of tables from query,
     * list can be empty.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<List<T>> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
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
    public Single<List<T>> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
    }

    @NonNull
    @Override
    protected Interceptor getRealCallInterceptor() {
        return new RealCallInterceptor();
    }

    private class RealCallInterceptor implements Interceptor {
        @SuppressWarnings({"TryFinallyCanBeTryWithResources", "unchecked"})
        // Min SDK :( unchecked for empty list
        @NonNull
        @Override
        public <Result, WrappedResult, Data> Result intercept(@NonNull PreparedOperation<Result, WrappedResult, Data> operation, @NonNull Chain chain) {
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
                        return (Result) EMPTY_LIST; // it's immutable
                    }

                    final List<T> list = new ArrayList<T>(count);

                    while (cursor.moveToNext()) {
                        list.add(getResolver.mapFromCursor(storIOSQLite, cursor));
                    }

                    return (Result) unmodifiableList(list);
                } finally {
                    cursor.close();
                }
            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Get operation. query = " + (query != null ? query : rawQuery), exception);
            }
        }
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
