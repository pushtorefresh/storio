package com.pushtorefresh.storio.contentresolver.operations.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operations.PreparedWriteOperation;

import java.util.Collection;

/**
 * Represents an Operation for {@link StorIOContentResolver} which performs insert or update data
 * in {@link android.content.ContentProvider}.
 *
 */
public abstract class PreparedPut<Result> implements PreparedWriteOperation<Result> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    protected PreparedPut(@NonNull StorIOContentResolver storIOContentResolver) {
        this.storIOContentResolver = storIOContentResolver;
    }

    /**
     * Builder for {@link PreparedPut}.
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Prepares Put Operation that should put one object.
         *
         * @param object object to put.
         * @param <T>    type of object.
         * @return builder for {@link PreparedPutObject}.
         */
        @NonNull
        public <T> PreparedPutObject.Builder<T> object(@NonNull T object) {
            return new PreparedPutObject.Builder<T>(storIOContentResolver, object);
        }

        /**
         * Prepares Put Operation that should put multiple objects.
         *
         * @param objects objects to put.
         * @param <T>     type of objects.
         * @return builder for {@link PreparedPutCollectionOfObjects}.
         */
        @NonNull
        public <T> PreparedPutCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedPutCollectionOfObjects.Builder<T>(storIOContentResolver, objects);
        }

        /**
         * Prepares Put Operation that should put one instance of {@link ContentValues}.
         *
         * @param contentValues non-null content values to put.
         * @return builder for {@link PreparedPutContentValues}.
         */
        @NonNull
        public PreparedPutContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutContentValues.Builder(storIOContentResolver, contentValues);
        }

        /**
         * Prepares Put Operation that should put several instances of {@link ContentValues}.
         *
         * @param contentValues non-null collection of {@link ContentValues}.
         * @return builder for {@link PreparedPutContentValuesIterable}.
         */
        @NonNull
        public PreparedPutContentValuesIterable.Builder contentValues(@NonNull Iterable<ContentValues> contentValues) {
            return new PreparedPutContentValuesIterable.Builder(storIOContentResolver, contentValues);
        }
    }
}
