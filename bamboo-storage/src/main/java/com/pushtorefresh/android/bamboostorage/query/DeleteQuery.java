package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DeleteQuery {

    @NonNull public final String tableName;

    @Nullable public final String where;

    @Nullable public final String[] whereArgs;

    public DeleteQuery(@NonNull String tableName, @Nullable String where, @Nullable String[] whereArgs) {
        this.tableName = tableName;
        this.where = where;
        this.whereArgs = whereArgs;
    }
}