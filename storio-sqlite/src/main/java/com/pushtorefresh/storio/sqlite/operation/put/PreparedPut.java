package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLiteDb;
import com.pushtorefresh.storio.operation.PreparedOperation;

import java.util.Arrays;

/**
 * Represents an Operation for {@link StorIOSQLiteDb} which performs insert or update data
 * in {@link StorIOSQLiteDb}
 *
 * @param <T> type of data you want to put
 */
public abstract class PreparedPut<T, Result> implements PreparedOperation<Result> {

    @NonNull
    protected final StorIOSQLiteDb storIOSQLiteDb;

    @NonNull
    protected final PutResolver<T> putResolver;

    PreparedPut(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull PutResolver<T> putResolver) {
        this.storIOSQLiteDb = storIOSQLiteDb;
        this.putResolver = putResolver;
    }

    /**
     * Builder for {@link PreparedPut}
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLiteDb storIOSQLiteDb;

        public Builder(@NonNull StorIOSQLiteDb storIOSQLiteDb) {
            this.storIOSQLiteDb = storIOSQLiteDb;
        }

        /**
         * Prepares Put Operation for one instance of {@link ContentValues}
         *
         * @param contentValues content values to put
         * @return builder
         */
        @NonNull
        public PreparedPutContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutContentValues.Builder(storIOSQLiteDb, contentValues);
        }

        /**
         * Prepares Put Operation for multiple {@link ContentValues}
         *
         * @param contentValuesIterable content values to put
         * @return builder
         */
        @NonNull
        public PreparedPutIterableContentValues.Builder contentValues(@NonNull Iterable<ContentValues> contentValuesIterable) {
            return new PreparedPutIterableContentValues.Builder(storIOSQLiteDb, contentValuesIterable);
        }

        /**
         * Prepares Put Operation for multiple {@link ContentValues}
         *
         * @param contentValuesArray content values to put
         * @return builder
         */
        @NonNull
        public PreparedPutIterableContentValues.Builder contentValues(@NonNull ContentValues... contentValuesArray) {
            return new PreparedPutIterableContentValues.Builder(storIOSQLiteDb, Arrays.asList(contentValuesArray));
        }

        /**
         * Prepares Put Operation for one object
         *
         * @param object object to put
         * @param <T>    type of object
         * @return builder
         */
        @NonNull
        public <T> PreparedPutObject.Builder<T> object(T object) {
            return new PreparedPutObject.Builder<T>(storIOSQLiteDb, object);
        }

        /**
         * Prepares Put Operation for multiple objects
         *
         * @param objects objects to put
         * @param <T>     type of objects
         * @return builder
         */
        @NonNull
        public <T> PreparedPutObjects.Builder<T> objects(@NonNull Iterable<T> objects) {
            return new PreparedPutObjects.Builder<T>(storIOSQLiteDb, objects);
        }

        /**
         * Prepares Put Operation for multiple objects
         *
         * @param objects objects to put
         * @param <T>     type of objects
         * @return builder
         */
        @SuppressWarnings("unchecked")
        @NonNull
        public final <T> PreparedPutObjects.Builder<T> objects(@NonNull T... objects) {
            return new PreparedPutObjects.Builder<T>(storIOSQLiteDb, Arrays.asList(objects));
        }
    }
}
