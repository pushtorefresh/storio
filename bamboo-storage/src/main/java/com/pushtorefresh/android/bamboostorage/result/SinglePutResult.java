package com.pushtorefresh.android.bamboostorage.result;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.Query;
import com.pushtorefresh.android.bamboostorage.wtf.QueryBuilder;

public class SinglePutResult<T extends BambooStorableType> {

    @Nullable private final Long insertedId;
    private final int updatedCount;

    public SinglePutResult(@NonNull BambooStorage bambooStorage, T object) {
        if (object.getStorableId() == null) {
            insertedId = bambooStorage.getInternal().insert(object);
            updatedCount = 0;
        } else {
            Query query = new QueryBuilder()
                    .where(bambooStorage.getInternal().getStorableIdFieldName(object.getClass()))
                    .whereArgs(String.valueOf(object.getStorableId()))
                    .build();

            updatedCount = bambooStorage.getInternal().update(object, query.where, query.whereArgs);
            insertedId   = null;
        }
    }

    public boolean wasInserted() {
        return insertedId != null;
    }

    public boolean wasUpdated() {
        return updatedCount > 0;
    }

    @Nullable public Long getInsertedId() {
        return insertedId;
    }

    public int getUpdatedCount() {
        return updatedCount;
    }
}
