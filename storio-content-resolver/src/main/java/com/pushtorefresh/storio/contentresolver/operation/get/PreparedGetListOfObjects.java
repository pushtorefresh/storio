package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeDefaults;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.operation.MapFunc;
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
     * Compile-time safe part of builder for {@link PreparedGetListOfObjects}
     *
     * @param <T> type of objects for query
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
         * Required: Specifies {@link Query} for Get Operation
         *
         * @param query query
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify query");
            return new CompleteBuilder<T>(storIOContentResolver, type, query);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedGetListOfObjects}
     *
     * @param <T> type of objects for query
     */
    public static class CompleteBuilder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Class<T> type;

        @NonNull
        private final Query query;

        private MapFunc<Cursor, T> mapFunc;
        private GetResolver getResolver;

        CompleteBuilder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type, @NonNull Query query) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
            this.query = query;
        }

        /**
         * Optional: Specifies map function for Get Operation
         * which will map {@link Cursor} to object of required type
         * <p/>
         * Can be set via {@link ContentResolverTypeDefaults},
         * if value is not set via {@link ContentResolverTypeDefaults} or explicitly exception will be thrown
         *
         * @param mapFunc map function which will map {@link Cursor} to object of required type
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withMapFunc(@Nullable MapFunc<Cursor, T> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        /**
         * Optional: Specifies {@link GetResolver} for Get Operation
         * which allows you to customize behavior of Get Operation
         * <p/>
         * Can be set via {@link ContentResolverTypeDefaults},
         * If value is not set via {@link ContentResolverTypeDefaults}, instance of {@link DefaultGetResolver}
         *
         * @param getResolver get resolver
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
            final ContentResolverTypeDefaults<T> typeDefaults = storIOContentResolver.internal().typeDefaults(type);

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

            return new PreparedGetListOfObjects<T>(
                    storIOContentResolver,
                    getResolver,
                    mapFunc,
                    query
            );
        }
    }
}