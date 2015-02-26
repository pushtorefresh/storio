package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DeleteQuery {

    @NonNull public final String table;

    @Nullable public final String where;

    @Nullable public final String[] whereArgs;

    public DeleteQuery(@NonNull String table, @Nullable String where, @Nullable String[] whereArgs) {
        this.table = table;
        this.where = where;
        this.whereArgs = whereArgs;
    }
}