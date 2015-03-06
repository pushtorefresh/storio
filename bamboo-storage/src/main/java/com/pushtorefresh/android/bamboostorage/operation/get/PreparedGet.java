package com.pushtorefresh.android.bamboostorage.operation.get;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.query.Query;
import com.pushtorefresh.android.bamboostorage.query.RawQuery;

public abstract class PreparedGet<T> implements PreparedOperation<T> {

    @NonNull  protected final BambooStorage bambooStorage;
    @Nullable protected final Query query;
    @Nullable protected final RawQuery rawQuery;

    PreparedGet(@NonNull BambooStorage bambooStorage, @NonNull Query query) {
        this.bambooStorage = bambooStorage;
        this.query = query;
        this.rawQuery = null;
    }

    PreparedGet(@NonNull BambooStorage bambooStorage, @NonNull RawQuery rawQuery) {
        this.bambooStorage = bambooStorage;
        this.rawQuery = rawQuery;
        query = null;
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(bambooStorage);
        }

        @NonNull public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<>(bambooStorage, type);
        }
    }

}
