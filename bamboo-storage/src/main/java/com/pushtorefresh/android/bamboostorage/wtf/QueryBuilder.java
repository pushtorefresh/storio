package com.pushtorefresh.android.bamboostorage.wtf;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class QueryBuilder {

    private static final Query ALL_FIELDS_NULL_QUERY = new Query(null, null, null);

    @NonNull public static Query allFieldsNull() {
        return ALL_FIELDS_NULL_QUERY;
    }

    @Nullable private String where;
    @Nullable private String[] whereArgs;
    @Nullable private String orderBy;

    @NonNull public QueryBuilder where(@Nullable String where) {
        this.where = where;
        return this;
    }

    @NonNull public QueryBuilder whereArgs(@Nullable String... whereArgs) {
        this.whereArgs = whereArgs;
        return this;
    }

    @NonNull public QueryBuilder orderBy(@Nullable String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    @NonNull public Query build() {
        return new Query(where, whereArgs, orderBy);
    }

}
