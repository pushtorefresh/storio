package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InsertQuery {

    @NonNull public final String table;

    @Nullable public final String nullColumnHack;

    public InsertQuery(@NonNull String table, @Nullable String nullColumnHack) {
        this.table = table;
        this.nullColumnHack = nullColumnHack;
    }
}
