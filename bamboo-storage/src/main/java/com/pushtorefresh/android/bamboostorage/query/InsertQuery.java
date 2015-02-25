package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InsertQuery {

    @NonNull public final String tableName;

    @Nullable public final String nullColumnHack;

    public InsertQuery(@NonNull String tableName, @Nullable String nullColumnHack) {
        this.tableName = tableName;
        this.nullColumnHack = nullColumnHack;
    }
}
