package com.pushtorefresh.android.bamboostorage.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class QueryBuilder {

    private boolean distinct;
    private String table;
    private String[] columns;
    private String selection;
    private String where;
    private String[] whereArgs;
    private String groupBy;
    private String having;
    private String orderBy;
    private String limit;
    
    @NonNull public QueryBuilder distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    @NonNull public QueryBuilder table(@NonNull String table) {
        this.table = table;
        return this;
    }
    
    @NonNull public QueryBuilder columns(@Nullable String... columns) {
        this.columns = columns;
        return this;
    }

    @NonNull public QueryBuilder selection(@Nullable String selection) {
        this.selection = selection;
        return this;
    }

    @NonNull public QueryBuilder where(@Nullable String where) {
        this.where = where;
        return this;
    }

    @NonNull public QueryBuilder whereArgs(@Nullable String... whereArgs) {
        this.whereArgs = whereArgs;
        return this;
    }

    @NonNull public QueryBuilder groupBy(@Nullable String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    @NonNull public QueryBuilder having(@Nullable String having) {
        this.having = having;
        return this;
    }

    @NonNull public QueryBuilder orderBy(@Nullable String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    @NonNull public QueryBuilder limit(@Nullable String limit) {
        this.limit = limit;
        return this;
    }

    @NonNull public Query build() {
        return new Query(
                distinct,
                table,
                columns,
                selection,
                where,
                whereArgs,
                groupBy,
                having,
                orderBy,
                limit
        );
    }
}
