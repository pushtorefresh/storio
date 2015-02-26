package com.pushtorefresh.android.bamboostorage.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.query.Query;

import java.util.List;

public abstract class PreparedGet<T> implements PreparedOperation<T> {

    @NonNull protected final BambooStorage bambooStorage;
    @NonNull protected final Query query;

    public PreparedGet(@NonNull BambooStorage bambooStorage, @NonNull Query query) {
        this.bambooStorage = bambooStorage;
        this.query = query;
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;
        private Query query;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public Builder query(@NonNull Query query) {
            this.query = query;
            return this;
        }

        protected void validateFields() {
            //noinspection ConstantConditions
            if (query == null) {
                throw new IllegalStateException("Please set query object");
            }
        }

        @NonNull public PreparedOperation<Cursor> resultAsCursor() {
            validateFields();
            return new PreparedGetWithResultAsCursor(bambooStorage, query);
        }

        @NonNull public <T> PreparedOperation<List<T>> resultAsObjects(@NonNull MapFunc<Cursor, T> mapFunc) {
            validateFields();
            return new PreparedGetWithResultsAsObjects<>(bambooStorage, query, mapFunc);
        }
    }

}
