package com.pushtorefresh.storio.contentprovider.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentprovider.Changes;
import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.contentprovider.query.Query;
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
 * Represents an Operation for {@link StorIOContentProvider} which performs query that retrieves data as list of objects
 * from {@link android.content.ContentProvider}
 *
 * @param <T> type of result
 */
public class PreparedGetListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull
    private final MapFunc<Cursor, T> mapFunc;

    @NonNull
    private final Query query;

    PreparedGetListOfObjects(@NonNull StorIOContentProvider storIOContentProvider, @NonNull GetResolver getResolver, @NonNull MapFunc<Cursor, T> mapFunc, @NonNull Query query) {
        super(storIOContentProvider, getResolver);
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
        final Cursor cursor = getResolver.performGet(storIOContentProvider, query);

        try {
            if (cursor == null) {
                return new ArrayList<>(0);
            } else {
                final List<T> list = new ArrayList<>(cursor.getCount());

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

        return storIOContentProvider
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
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOContentProvider storIOContentProvider;

        @NonNull
        private final Class<T> type; // currently type not used as object, only for generic Builder class

        private MapFunc<Cursor, T> mapFunc;
        private Query query;
        private GetResolver getResolver;

        public Builder(@NonNull StorIOContentProvider storIOContentProvider, @NonNull Class<T> type) {
            this.storIOContentProvider = storIOContentProvider;
            this.type = type;
        }

        /**
         * Specifies map function for Get Operation which will map {@link Cursor} to object of required type
         *
         * @param mapFunc map function which will map {@link Cursor} to object of required type
         * @return builder
         */
        @NonNull
        public Builder<T> withMapFunc(@NonNull MapFunc<Cursor, T> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        /**
         * Specifies {@link Query} for Get Operation
         *
         * @param query query
         * @return builder
         */
        @NonNull
        public Builder<T> withQuery(@NonNull Query query) {
            this.query = query;
            return this;
        }

        /**
         * Optional: Specifies {@link GetResolver} for Get Operation which allows you to customize behavior of Get Operation
         *
         * @param getResolver get resolver
         * @return builder
         */
        @NonNull
        public Builder<T> withGetResolver(@Nullable GetResolver getResolver) {
            this.getResolver = getResolver;
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

            return new PreparedGetListOfObjects<>(
                    storIOContentProvider,
                    getResolver,
                    mapFunc,
                    query
            );
        }
    }
}
