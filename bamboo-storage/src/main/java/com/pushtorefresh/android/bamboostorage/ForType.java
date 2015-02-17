package com.pushtorefresh.android.bamboostorage;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.result.IterableQueryResult;
import com.pushtorefresh.android.bamboostorage.result.MultiplePutResult;
import com.pushtorefresh.android.bamboostorage.result.SinglePutResult;
import com.pushtorefresh.android.bamboostorage.result.SingleQueryResult;
import com.pushtorefresh.android.bamboostorage.result.SingleDeleteResult;
import com.pushtorefresh.android.bamboostorage.wtf.Query;
import com.pushtorefresh.android.bamboostorage.wtf.QueryBuilder;

public class ForType<T extends BambooStorableType> {

    @NonNull private final BambooStorage bambooStorage;
    @NonNull private final Class<T> type;

    public ForType(@NonNull BambooStorage bambooStorage, @NonNull Class<T> type) {
        this.bambooStorage = bambooStorage;
        this.type = type;
    }

    @NonNull public SinglePutResult<T> put(@NonNull T object) {
        return new SinglePutResult<>(bambooStorage, object);
    }

    @NonNull public MultiplePutResult<T> putAll(@NonNull Iterable<T> objects) {
        return new MultiplePutResult<>(bambooStorage, objects);
    }

    @NonNull public IterableQueryResult<T> getAll() {
        return new IterableQueryResult<>(bambooStorage, type, QueryBuilder.allFieldsNull());
    }

    @NonNull public IterableQueryResult<T> getAll(@NonNull Query query) {
        return new IterableQueryResult<>(bambooStorage, type, query);
    }

    @NonNull public SingleQueryResult<T> getFirst() {
        return new SingleQueryResult<>(bambooStorage, type, QueryBuilder.allFieldsNull());
    }

    @NonNull public SingleQueryResult<T> getFirst(@NonNull Query query) {
        return new SingleQueryResult<>(bambooStorage, type, query);
    }

    @NonNull public SingleQueryResult<T> getLast() {
        return new SingleQueryResult<>(bambooStorage, type, QueryBuilder.allFieldsNull());
    }

    @NonNull public SingleQueryResult<T> getLast(@NonNull Query query) {
        return new SingleQueryResult<>(bambooStorage, type, query);
    }

    @NonNull public SingleDeleteResult<T> delete(@NonNull T object) {
        return new SingleDeleteResult<>(bambooStorage, object);
    }

    @NonNull public SingleDeleteResult<T> delete(@NonNull Query query) {
        return new SingleDeleteResult<>(bambooStorage, type, query);
    }
}
