package com.pushtorefresh.storio.contentprovider.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.contentprovider.query.Query;
import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;

import java.util.List;

import rx.Observable;

/**
 * Represents an Operation for {@link StorIOContentProvider} which performs query that retrieves data as list of objects
 * from {@link android.content.ContentProvider}
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

    @Nullable
    @Override
    public List<T> executeAsBlocking() {
        return null;
    }

    @NonNull
    @Override
    public Observable<List<T>> createObservable() {
        return null;
    }

    @NonNull
    @Override
    public Observable<List<T>> createObservableStream() {
        return null;
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
            if (mapFunc == null) {
                throw new IllegalStateException("Please specify map function");
            }

            if (query == null) {
                throw new IllegalStateException("Please specify query");
            }

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
