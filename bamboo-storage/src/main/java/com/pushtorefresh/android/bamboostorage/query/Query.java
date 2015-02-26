package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Query {

    public final boolean distinct;

    @NonNull public final String table;

    @Nullable public final String[] columns;

    @Nullable public final String selection;

    @Nullable public final String where;

    @Nullable public final String[] whereArgs;

    @Nullable public final String groupBy;

    @Nullable public final String having;

    @Nullable public final String orderBy;

    @Nullable public final String limit;

    public Query(boolean distinct, @NonNull String table, @Nullable String[] columns,
                 @Nullable String selection, @Nullable String where, @Nullable String[] whereArgs,
                 @Nullable String groupBy, @Nullable String having,
                 @Nullable String orderBy, @Nullable String limit) {
        this.distinct = distinct;
        this.table = table;
        this.columns = columns;
        this.selection = selection;
        this.where = where;
        this.whereArgs = whereArgs;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.limit = limit;
    }
}
