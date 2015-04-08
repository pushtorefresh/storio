package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Represents an Operation for {@link StorIOContentResolver} which performs query that retrieves data as list of objects
 * from {@link android.content.ContentProvider}
 *
 * @param <T> type of result
 */
public class PreparedGetListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull
    private final MapFunc<Cursor, T> mapFunc;

    @NonNull
    private final Query query;

    PreparedGetListOfObjects(@NonNull StorIOContentResolver storIOContentResolver, @NonNull GetResolver getResolver, @NonNull MapFunc<Cursor, T> mapFunc, @NonNull Query query) {
        super(storIOContentResolver, getResolver);
        this.mapFunc = mapFunc;
        this.query = query;
    }

    /**
     * Executes Prepared Operation immediately in current thread
     *
     * @return non-null list with mapped results, can be empty
     */
    @Nullable
    @Override
    public List<T> executeAsBlocking() {
        final Cursor cursor = getResolver.performGet(storIOContentResolver, query);

        try {
            if (cursor == null) {
                return new ArrayList<T>(0);
            } else {
                final List<T> list = new ArrayList<T>(cursor.getCount());

                while (cursor.moveToNext()) {
                    list.add(mapFunc.map(cursor));
                }

                return list;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Creates an {@link Observable} which will emit result of operation
     *
     * @return non-null {@link Observable} which will emit non-null list with mapped results, list can be empty
     */
    @NonNull
    @Override
    public Observable<List<T>> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(Subscriber<? super List<T>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Creates an {@link Observable} which will be subscribed to changes of {@link #query} Uri
     * and will emit result each time change occurs
     * <p/>
     * First result will be emitted immediately,
     * other emissions will occur only if changes of {@link #query} Uri will occur
     *
     * @return non-null {@link Observable} which will emit non-null list with mapped results and will be subscribed to changes of {@link #query} Uri
     */
    @NonNull
    @Override
    public Observable<List<T>> createObservableStream() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return storIOContentResolver
                .observeChangesOfUri(query.uri)
                .map(new Func1<Changes, List<T>>() {
                    @Override
                    public List<T> call(Changes changes) { // each change triggers executeAsBlocking
                        return executeAsBlocking();
                    }
                })
                .startWith(executeAsBlocking());  // start stream with first query result
    }

    /**
     * Builder for {@link PreparedGetListOfObjects}
     * <p>
     * Required: You should specify query see {@link #withQuery(Query)}
     *
     * @param <T> type of objects for query
     */
    public static class Builder<T> {

        @NonNull
        final StorIOContentResolver storIOContentResolver;

        @NonNull
        final Class<T> type; // currently type not used as object, only for generic Builder class

        MapFunc<Cursor, T> mapFunc;
        Query query;
        GetResolver getResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
        }

        /**
         * Required: Specifies {@link Query} for Get Operation
         *
         * @param query query
         * @return builder
         */
        @NonNull
        public MapFuncBuilder<T> withQuery(@NonNull Query query) {
            this.query = query;
            return new MapFuncBuilder<T>(this);
        }

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
        public Builder<T> withGetResolver(@Nullable GetResolver getResolver) {
            this.getResolver = getResolver;
            return this;
        }
    }

    /**
     * Builder for {@link PreparedGetListOfObjects}
     * <p>
     * Required: You should specify map function see {@link #withMapFunc(MapFunc)}
     *
     * @param <T> type of objects for query
     */
    public static class MapFuncBuilder<T> extends Builder<T> {

        MapFuncBuilder(@NonNull final Builder<T> builder) {
            super(builder.storIOContentResolver, builder.type);

            query = builder.query;
            getResolver = builder.getResolver;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public MapFuncBuilder<T> withQuery(@NonNull Query query) {
            super.withQuery(query);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public MapFuncBuilder<T> withGetResolver(@Nullable GetResolver getResolver) {
            super.withGetResolver(getResolver);
            return this;
        }

        /**
         * Required: Specifies map function for Get Operation
         * which will map {@link Cursor} to object of required type
         *
         * @param mapFunc map function which will map {@link Cursor} to object of required type
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withMapFunc(@NonNull MapFunc<Cursor, T> mapFunc) {
            this.mapFunc = mapFunc;
            return new CompleteBuilder<T>(this);
        }
    }

    /**
     * Builder for {@link PreparedGetListOfObjects}

     * @param <T> type of objects for query
     */
    public static class CompleteBuilder<T> extends MapFuncBuilder<T> {

        CompleteBuilder(@NonNull final Builder<T> builder) {
            super(builder);

            mapFunc = builder.mapFunc;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public CompleteBuilder<T> withMapFunc(@NonNull MapFunc<Cursor, T> mapFunc) {
            super.withMapFunc(mapFunc);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override public CompleteBuilder<T> withQuery(@NonNull Query query) {
            super.withQuery(query);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public CompleteBuilder<T> withGetResolver(@Nullable GetResolver getResolver) {
            super.withGetResolver(getResolver);
            return this;
        }

        /**
         * Prepares Get Operation
         *
         * @return {@link PreparedGetListOfObjects} instance
         */
        @NonNull
        public PreparedOperationWithReactiveStream<List<T>> prepare() {
            checkNotNull(mapFunc, "Please specify map function");
            checkNotNull(query, "Please specify query");

            if (getResolver == null) {
                getResolver = DefaultGetResolver.INSTANCE;
            }

            return new PreparedGetListOfObjects<T>(
                    storIOContentResolver,
                    getResolver,
                    mapFunc,
                    query
            );
        }
    }
}