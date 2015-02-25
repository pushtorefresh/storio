package com.pushtorefresh.android.bamboostorage;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.result.MultipleDeleteResult;
import com.pushtorefresh.android.bamboostorage.result.MultiplePutResult;
import com.pushtorefresh.android.bamboostorage.result.SingleDeleteResult;
import com.pushtorefresh.android.bamboostorage.result.SinglePutResult;
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

    @NonNull public SingleDeleteResult<T> delete(@NonNull T object) {
        return new SingleDeleteResult<>(bambooStorage, object);
    }

    @NonNull public SingleDeleteResult<T> delete(@NonNull Query query) {
        return new SingleDeleteResult<>(bambooStorage, type, query);
    }

    @NonNull public MultipleDeleteResult<T> deleteAll(@NonNull Iterable<T> objects) {
        return new MultipleDeleteResult<>(bambooStorage, objects);
    }

    @NonNull public SingleDeleteResult<T> deleteAll() {
        return new SingleDeleteResult<>(bambooStorage, type, QueryBuilder.allFieldsNull());
    }
}
