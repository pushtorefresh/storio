package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

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

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        if (distinct != query.distinct) return false;
        if (!Arrays.equals(columns, query.columns)) return false;
        if (groupBy != null ? !groupBy.equals(query.groupBy) : query.groupBy != null) return false;
        if (having != null ? !having.equals(query.having) : query.having != null) return false;
        if (limit != null ? !limit.equals(query.limit) : query.limit != null) return false;
        if (orderBy != null ? !orderBy.equals(query.orderBy) : query.orderBy != null) return false;
        if (selection != null ? !selection.equals(query.selection) : query.selection != null)
            return false;
        if (!table.equals(query.table)) return false;
        if (where != null ? !where.equals(query.where) : query.where != null) return false;
        if (!Arrays.equals(whereArgs, query.whereArgs)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (distinct ? 1 : 0);
        result = 31 * result + table.hashCode();
        result = 31 * result + (columns != null ? Arrays.hashCode(columns) : 0);
        result = 31 * result + (selection != null ? selection.hashCode() : 0);
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? Arrays.hashCode(whereArgs) : 0);
        result = 31 * result + (groupBy != null ? groupBy.hashCode() : 0);
        result = 31 * result + (having != null ? having.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        return result;
    }
}
