package com.pushtorefresh.android.bamboostorage.wtf;

import android.support.annotation.Nullable;

public class Query {
    @Nullable public final String where;
    @Nullable public final String[] whereArgs;
    @Nullable public final String orderBy;

    Query(@Nullable String where, @Nullable String[] whereArgs, @Nullable String orderBy) {
        this.where = where;
        this.whereArgs = whereArgs;
        this.orderBy = orderBy;
    }
}
