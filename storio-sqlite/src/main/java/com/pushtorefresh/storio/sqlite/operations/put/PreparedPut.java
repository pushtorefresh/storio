package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedWriteOperation;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.Arrays;
import java.util.Collection;

/**
 * Prepared Put Operation for {@link StorIOSQLite} which performs insert or update data
 * in {@link StorIOSQLite}.
 */
public abstract class PreparedPut<Result> implements PreparedWriteOperation<Result> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    PreparedPut(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
    }

    /**
     * Builder for {@link PreparedPut}.
     */
    public static class Builder {

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
         * @param objects objects to put.
         * @param <T>     type of objects.
         * @return builder.
         */
        @NonNull
        public <T> PreparedPutCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedPutCollectionOfObjects.Builder<T>(storIOSQLite, objects);
        }
    }
}
