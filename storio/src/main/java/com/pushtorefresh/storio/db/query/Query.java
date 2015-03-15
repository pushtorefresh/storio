package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

public class Query {

    public final boolean distinct;

    @NonNull public final String table;

    @Nullable public final String[] columns;

    @Nullable public final String where;

    @Nullable public final String[] whereArgs;

    @Nullable public final String groupBy;

    @Nullable public final String having;

    @Nullable public final String orderBy;

    @Nullable public final String limit;

    /**
     * Please use {@link com.pushtorefresh.storio.db.query.Query.Builder} instead of constructor
     */
    public Query(boolean distinct, @NonNull String table, @Nullable String[] columns,
                 @Nullable String where, @Nullable String[] whereArgs,
                 @Nullable String groupBy, @Nullable String having,
                 @Nullable String orderBy, @Nullable String limit) {
        this.distinct = distinct;
        this.table = table;
        this.columns = columns;
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
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? Arrays.hashCode(whereArgs) : 0);
        result = 31 * result + (groupBy != null ? groupBy.hashCode() : 0);
        result = 31 * result + (having != null ? having.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Query{" +
                "distinct=" + distinct +
                ", table='" + table + '\'' +
                ", columns=" + Arrays.toString(columns) +
                ", where='" + where + '\'' +
                ", whereArgs=" + Arrays.toString(whereArgs) +
                ", groupBy='" + groupBy + '\'' +
                ", having='" + having + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", limit='" + limit + '\'' +
                '}';
    }

    public static class Builder {

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

        @NonNull public Builder distinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }

        @NonNull public Builder table(@NonNull String table) {
            this.table = table;
            return this;
        }

        @NonNull public Builder columns(@Nullable String... columns) {
            this.columns = columns;
            return this;
        }

        @NonNull public Builder selection(@Nullable String selection) {
            this.selection = selection;
            return this;
        }

        @NonNull public Builder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        @NonNull public Builder whereArgs(@Nullable String... whereArgs) {
            this.whereArgs = whereArgs;
            return this;
        }

        @NonNull public Builder groupBy(@Nullable String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        @NonNull public Builder having(@Nullable String having) {
            this.having = having;
            return this;
        }

        @NonNull public Builder orderBy(@Nullable String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        @NonNull public Builder limit(@Nullable String limit) {
            this.limit = limit;
            return this;
        }

        @NonNull public Query build() {
            if (table == null || table.length() == 0) {
                throw new IllegalStateException("Please specify table name");
            }

            return new Query(
                    distinct,
                    table,
                    columns,
                    where,
                    whereArgs,
                    groupBy,
                    having,
                    orderBy,
                    limit
            );
        }
    }
}
