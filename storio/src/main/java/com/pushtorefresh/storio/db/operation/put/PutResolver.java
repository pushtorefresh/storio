package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;

public interface PutResolver<T> {

    @NonNull PutResult performPut(@NonNull StorIODb storIODb, @NonNull ContentValues contentValues);

    void afterPut(@NonNull T object, @NonNull PutResult putResult);
}
