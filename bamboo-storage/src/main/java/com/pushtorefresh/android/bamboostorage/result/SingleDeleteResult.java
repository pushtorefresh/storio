package com.pushtorefresh.android.bamboostorage.result;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.Query;

public class SingleDeleteResult<T extends BambooStorableType> {

    private final int deletedCount;

    public SingleDeleteResult(@NonNull BambooStorage bambooStorage, @NonNull T object) {
        deletedCount = bambooStorage.getInternal().delete(object);
    }

    public SingleDeleteResult(@NonNull BambooStorage bambooStorage, @NonNull Class<T> type, @NonNull Query query) {
        deletedCount = bambooStorage.getInternal().delete(type, query.where, query.whereArgs);
    }

    public int getDeletedCount() {
        return deletedCount;
    }

    public boolean wasDeleted() {
        return deletedCount > 0;
    }

    public boolean wasNotDeleted() {
        return deletedCount <= 0;
    }
}
