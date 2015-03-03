package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;

import java.util.Collection;

public abstract class PreparedPut<T> implements PreparedOperation<T> {

    @NonNull protected final BambooStorage bambooStorage;

    public PreparedPut(@NonNull BambooStorage bambooStorage) {
        this.bambooStorage = bambooStorage;
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public PreparedPutWithContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutWithContentValues.Builder(bambooStorage, contentValues);
        }

        @NonNull public <T> PreparedPutWithObject.Builder<T> object(T object) {
            return new PreparedPutWithObject.Builder<>(bambooStorage, object);
        }

        @NonNull public <T> PreparedPutCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedPutCollectionOfObjects.Builder<>(bambooStorage, objects);
        }
    }
}
