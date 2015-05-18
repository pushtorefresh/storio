package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.Arrays;

/**
 * Prepared Put Operation for {@link StorIOSQLite} which performs insert or update data
 * in {@link StorIOSQLite}.
 *
 * @param <T> type of data you want to put.
 */
public abstract class PreparedPut<T, Result> implements PreparedOperation<Result> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    @NonNull
    protected final PutResolver<T> putResolver;

    PreparedPut(@NonNull StorIOSQLite storIOSQLite, @NonNull PutResolver<T> putResolver) {
        this.storIOSQLite = storIOSQLite;
        this.putResolver = putResolver;
    }

    /**
     * Builder for {@link PreparedPut}.
     */
    public static final class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Prepares Put Operation for one instance of {@link ContentValues}.
         *
         * @param contentValues content values to put.
         * @return builder.
         */
        @NonNull
        public PreparedPutContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutContentValues.Builder(storIOSQLite, contentValues);
        }

        /**
         * Prepares Put Operation for multiple {@link ContentValues}.
         *
         * @param contentValuesIterable content values to put.
         * @return builder.
         */
        @NonNull
        public PreparedPutContentValuesIterable.Builder contentValues(@NonNull Iterable<ContentValues> contentValuesIterable) {
            return new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValuesIterable);
        }

        /**
         * Prepares Put Operation for multiple {@link ContentValues}.
         *
         * @param contentValuesArray content values to put.
         * @return builder.
         */
        @NonNull
        public PreparedPutContentValuesIterable.Builder contentValues(@NonNull ContentValues... contentValuesArray) {
            return new PreparedPutContentValuesIterable.Builder(storIOSQLite, Arrays.asList(contentValuesArray));
        }

        /**
         * Prepares Put Operation for one object.
         *
         * @param object object to put.
         * @param <T>    type of object.
         * @return builder.
         */
        @NonNull
        public <T> PreparedPutObject.Builder<T> object(T object) {
            return new PreparedPutObject.Builder<T>(storIOSQLite, object);
        }

        /**
         * Prepares Put Operation for multiple objects.
         *
         * @param type    type of objects, due to limitations of Generics in Java
         *                we have to explicitly ask you about type of objects, sorry :(.
         * @param objects objects to put.
         * @param <T>     type of objects.
         * @return builder.
         */
        @NonNull
        public <T> PreparedPutObjects.Builder<T> objects(@NonNull Class<T> type, @NonNull Iterable<T> objects) {
            return new PreparedPutObjects.Builder<T>(storIOSQLite, type, objects);
        }

        /**
         * Prepares Put Operation for multiple objects.
         *
         * @param type    type of objects, due to limitations of Generics in Java
         *                we have to explicitly ask you about type of objects, sorry :(.
         * @param objects objects to put.
         * @param <T>     type of objects.
         * @return builder.
         */
        @SuppressWarnings("unchecked")
        @NonNull
        public <T> PreparedPutObjects.Builder<T> objects(@NonNull Class<T> type, @NonNull T... objects) {
            return new PreparedPutObjects.Builder<T>(storIOSQLite, type, Arrays.asList(objects));
        }
    }
}
