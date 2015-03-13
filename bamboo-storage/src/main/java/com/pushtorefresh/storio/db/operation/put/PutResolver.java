package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.BambooStorageDb;

public interface PutResolver<T> {

    @NonNull PutResult performPut(@NonNull BambooStorageDb bambooStorageDb, @NonNull ContentValues contentValues);

    void afterPut(@NonNull T object, @NonNull PutResult putResult);
}
