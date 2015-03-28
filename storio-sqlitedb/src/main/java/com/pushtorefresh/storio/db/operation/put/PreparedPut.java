package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.operation.PreparedOperation;

import java.util.Arrays;

public abstract class PreparedPut<T, Result> implements PreparedOperation<Result> {

    @NonNull protected final StorIODb storIODb;
    @NonNull protected final PutResolver<T> putResolver;

    PreparedPut(@NonNull StorIODb storIODb, @NonNull PutResolver<T> putResolver) {
        this.storIODb = storIODb;
        this.putResolver = putResolver;
    }

    public static class Builder {

        @NonNull private final StorIODb storIODb;

        public Builder(@NonNull StorIODb storIODb) {
            this.storIODb = storIODb;
        }

        /**
         * Prepares Put Operation for one instance of {@link ContentValues}
         *
         * @param contentValues content values to put
         * @return builder
         */
        @NonNull
        public PreparedPutWithContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutWithContentValues.Builder(storIODb, contentValues);
        }

        /**
         * Prepares Put Operation for multiple {@link ContentValues}
         *
         * @param contentValuesIterable content values to put
         * @return builder
         */
        @NonNull
        public PreparedPutIterableContentValues.Builder contentValues(@NonNull Iterable<ContentValues> contentValuesIterable) {
            return new PreparedPutIterableContentValues.Builder(storIODb, contentValuesIterable);
        }

        /**
         * Prepares Put Operation for multiple {@link ContentValues}
         *
         * @param contentValuesArray content values to put
         * @return builder
         */
        @NonNull
        public PreparedPutIterableContentValues.Builder contentValues(@NonNull ContentValues... contentValuesArray) {
            return new PreparedPutIterableContentValues.Builder(storIODb, Arrays.asList(contentValuesArray));
        }

        /**
         * Prepares Put Operation for one object
         *
         * @param object object to put
         * @param <T>    type of object
         * @return builder
         */
        @NonNull public <T> PreparedPutWithObject.Builder<T> object(T object) {
            return new PreparedPutWithObject.Builder<>(storIODb, object);
        }

        /**
         * Prepares Put Operation for multiple objects
         *
         * @param objects objects to put
         * @param <T>     type of objects
         * @return builder
         */
        @NonNull public <T> PreparedPutObjects.Builder<T> objects(@NonNull Iterable<T> objects) {
            return new PreparedPutObjects.Builder<>(storIODb, objects);
        }

        /**
         * Prepares Put Operation for multiple objects
         *
         * @param objects objects to put
         * @param <T>     type of objects
         * @return builder
         */
        @SafeVarargs
        @NonNull public final <T> PreparedPutObjects.Builder<T> objects(@NonNull T... objects) {
            return new PreparedPutObjects.Builder<>(storIODb, Arrays.asList(objects));
        }
    }
}
