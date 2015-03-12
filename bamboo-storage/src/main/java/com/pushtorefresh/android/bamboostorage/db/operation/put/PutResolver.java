package com.pushtorefresh.android.bamboostorage.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorage;

public interface PutResolver<T> {

    @NonNull PutResult performPut(@NonNull BambooStorage bambooStorage, @NonNull ContentValues contentValues);

    void afterPut(@NonNull T object, @NonNull PutResult putResult);
}
