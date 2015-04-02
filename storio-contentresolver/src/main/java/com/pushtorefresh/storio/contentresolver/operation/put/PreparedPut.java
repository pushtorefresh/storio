package com.pushtorefresh.storio.contentresolver.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.PreparedOperation;

/**
 * Represents an Operation for {@link StorIOContentResolver} which performs insert or update data
 * in {@link android.content.ContentProvider}
 *
 * @param <T> type of data you want to put
 */
public abstract class PreparedPut<T, Result> implements PreparedOperation<Result> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    @NonNull
    protected final PutResolver<T> putResolver;

    protected PreparedPut(@NonNull StorIOContentResolver storIOContentResolver, @NonNull PutResolver<T> putResolver) {
        this.storIOContentResolver = storIOContentResolver;
        this.putResolver = putResolver;
    }

    /**
     * Builder for {@link PreparedPut}
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Prepares Put Operation that should put one object
         *
         * @param object object to put
         * @param <T>    type of object
         * @return builder for {@link PreparedPutObject}
         */
        @NonNull
        public <T> PreparedPutObject.Builder<T> object(@NonNull T object) {
            return new PreparedPutObject.Builder<>(storIOContentResolver, object);
        }

        /**
         * Prepares Put Operation that should put multiple objects
         *
         * @param objects objects to put
         * @param <T>     type of objects
         * @return builder for {@link PreparedPutObjects}
         */
        @NonNull
        public <T> PreparedPutObjects.Builder<T> objects(@NonNull Iterable<T> objects) {
            return new PreparedPutObjects.Builder<>(storIOContentResolver, objects);
        }
    }
}
