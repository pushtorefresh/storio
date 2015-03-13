package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.BambooStorageDb;
import com.pushtorefresh.storio.db.operation.PreparedOperation;

import java.util.Arrays;

public abstract class PreparedPut<T, Result> implements PreparedOperation<Result> {

    @NonNull protected final BambooStorageDb bambooStorageDb;
    @NonNull protected final PutResolver<T> putResolver;

    public PreparedPut(@NonNull BambooStorageDb bambooStorageDb, @NonNull PutResolver<T> putResolver) {
        this.bambooStorageDb = bambooStorageDb;
        this.putResolver = putResolver;
    }

    public static class Builder {

        @NonNull private final BambooStorageDb bambooStorageDb;

        public Builder(@NonNull BambooStorageDb bambooStorageDb) {
            this.bambooStorageDb = bambooStorageDb;
        }

        @NonNull public PreparedPutWithContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutWithContentValues.Builder(bambooStorageDb, contentValues);
        }

        @NonNull public PreparedPutIterableContentValues.Builder contentValues(@NonNull Iterable<ContentValues> contentValuesIterable) {
            return new PreparedPutIterableContentValues.Builder(bambooStorageDb, contentValuesIterable);
        }

        @NonNull public PreparedPutIterableContentValues.Builder contentValues(@NonNull ContentValues... contentValuesArray) {
            return new PreparedPutIterableContentValues.Builder(bambooStorageDb, Arrays.asList(contentValuesArray));
        }

        @NonNull public <T> PreparedPutWithObject.Builder<T> object(T object) {
            return new PreparedPutWithObject.Builder<>(bambooStorageDb, object);
        }

        @NonNull public <T> PreparedPutObjects.Builder<T> objects(@NonNull Iterable<T> objects) {
            return new PreparedPutObjects.Builder<>(bambooStorageDb, objects);
        }

        @SafeVarargs
        @NonNull public final <T> PreparedPutObjects.Builder<T> objects(@NonNull T... objects) {
            return new PreparedPutObjects.Builder<>(bambooStorageDb, Arrays.asList(objects));
        }
    }
}
