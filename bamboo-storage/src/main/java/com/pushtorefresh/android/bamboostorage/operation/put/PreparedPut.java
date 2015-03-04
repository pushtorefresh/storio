package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;

import java.util.Arrays;

public abstract class PreparedPut<T, Result> implements PreparedOperation<Result> {

    @NonNull protected final BambooStorage bambooStorage;
    @NonNull protected final PutResolver<T> putResolver;

    public PreparedPut(@NonNull BambooStorage bambooStorage, @NonNull PutResolver<T> putResolver) {
        this.bambooStorage = bambooStorage;
        this.putResolver = putResolver;
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public PreparedPutWithContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutWithContentValues.Builder(bambooStorage, contentValues);
        }

        @NonNull public PreparedPutIterableContentValues.Builder contentValues(@NonNull Iterable<ContentValues> contentValuesIterable) {
            return new PreparedPutIterableContentValues.Builder(bambooStorage, contentValuesIterable);
        }

        @NonNull public PreparedPutArrayContentValues.Builder contentValues(@NonNull ContentValues... contentValuesArray) {
            return new PreparedPutArrayContentValues.Builder(bambooStorage, contentValuesArray);
        }

        @NonNull public <T> PreparedPutWithObject.Builder<T> object(T object) {
            return new PreparedPutWithObject.Builder<>(bambooStorage, object);
        }

        @NonNull public <T> PreparedPutObjects.Builder<T> objects(@NonNull Iterable<T> objects) {
            return new PreparedPutObjects.Builder<>(bambooStorage, objects);
        }

        @NonNull public <T> PreparedPutObjects.Builder<T> objects(@NonNull T... objects) {
            return new PreparedPutObjects.Builder<>(bambooStorage, Arrays.asList(objects));
        }
    }
}
