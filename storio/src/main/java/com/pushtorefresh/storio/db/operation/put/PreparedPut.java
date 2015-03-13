package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.PreparedOperation;

import java.util.Arrays;

public abstract class PreparedPut<T, Result> implements PreparedOperation<Result> {

    @NonNull protected final StorIODb storIODb;
    @NonNull protected final PutResolver<T> putResolver;

    public PreparedPut(@NonNull StorIODb storIODb, @NonNull PutResolver<T> putResolver) {
        this.storIODb = storIODb;
        this.putResolver = putResolver;
    }

    public static class Builder {

        @NonNull private final StorIODb storIODb;

        public Builder(@NonNull StorIODb storIODb) {
            this.storIODb = storIODb;
        }

        @NonNull public PreparedPutWithContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutWithContentValues.Builder(storIODb, contentValues);
        }

        @NonNull public PreparedPutIterableContentValues.Builder contentValues(@NonNull Iterable<ContentValues> contentValuesIterable) {
            return new PreparedPutIterableContentValues.Builder(storIODb, contentValuesIterable);
        }

        @NonNull public PreparedPutIterableContentValues.Builder contentValues(@NonNull ContentValues... contentValuesArray) {
            return new PreparedPutIterableContentValues.Builder(storIODb, Arrays.asList(contentValuesArray));
        }

        @NonNull public <T> PreparedPutWithObject.Builder<T> object(T object) {
            return new PreparedPutWithObject.Builder<>(storIODb, object);
        }

        @NonNull public <T> PreparedPutObjects.Builder<T> objects(@NonNull Iterable<T> objects) {
            return new PreparedPutObjects.Builder<>(storIODb, objects);
        }

        @SafeVarargs
        @NonNull public final <T> PreparedPutObjects.Builder<T> objects(@NonNull T... objects) {
            return new PreparedPutObjects.Builder<>(storIODb, Arrays.asList(objects));
        }
    }
}
