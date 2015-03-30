package com.pushtorefresh.storio.sqlitedb.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.util.Checks;
import com.pushtorefresh.storio.util.QueryUtil;

import java.util.List;

/**
 * Get query for {@link com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb}
 * <p/>
 * Instances of this class are Immutable
 */
public class Query {

    /**
     * True if you want each row to be unique, false otherwise
     */
    public final boolean distinct;

    /**
     * Table name
     */
    @NonNull
    public final String table;

    /**
     * Optional list of columns that should be received.
     * <p/>
     * If list will be null or empty -> all columns will be received
     */
    @Nullable
    public final List<String> columns;

    /**
     * Optional filter declaring which rows to return
     * <p/>
     * Formatted as an SQL WHERE clause (excluding the WHERE itself).
     * <p/>
     * Passing null will return all rows for the given table
     */
    @Nullable
    public final String where;

    /**
     * Optional list of arguments for {@link #where} clause
     */
    @Nullable
    public final List<String> whereArgs;

    /**
     * Optional filter declaring how to group rows.
     * <p/>
     * Formatted as an SQL GROUP BY clause (excluding the GROUP BY itself).
     * <p/>
     * Passing null will cause the rows to not be grouped
     */
    @Nullable
    public final String groupBy;

    /**
     * Optional filter declare which row groups to include in the cursor, if row grouping is being used.
     * <p/>
     * Formatted as an SQL HAVING clause (excluding the HAVING itself).
     * <p/>
     * Passing null will cause all row groups to be included, and is required when row grouping is not being used
     */
    @Nullable
    public final String having;

    /**
     * Optional specifier to how to order the rows.
     * <p/>
     * Formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
     * <p/>
     * Passing null will use the default sort order, which may be unordered
     */
    @Nullable
    public final String orderBy;

    /**
     * Optional specifier that limits the number of rows returned by the query.
     * <p/>
     * Formatted as LIMIT clause.
     * <p/>
     * Passing null denotes no LIMIT clause
     */
    @Nullable
    public final String limit;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlitedb.query.Query.Builder} instead of constructor
     */
    protected Query(boolean distinct, @NonNull String table, @Nullable List<String> columns,
                    @Nullable String where, @Nullable List<String> whereArgs,
                    @Nullable String groupBy, @Nullable String having,
                    @Nullable String orderBy, @Nullable String limit) {
        this.distinct = distinct;
        this.table = table;
        this.columns = QueryUtil.listToUnmodifiable(columns);
        this.where = where;
        this.whereArgs = QueryUtil.listToUnmodifiable(whereArgs);
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        if (distinct != query.distinct) return false;
        if (!table.equals(query.table)) return false;
        if (columns != null ? !columns.equals(query.columns) : query.columns != null) return false;
        if (where != null ? !where.equals(query.where) : query.where != null) return false;
        if (whereArgs != null ? !whereArgs.equals(query.whereArgs) : query.whereArgs != null)
            return false;
        if (groupBy != null ? !groupBy.equals(query.groupBy) : query.groupBy != null) return false;
        if (having != null ? !having.equals(query.having) : query.having != null) return false;
        if (orderBy != null ? !orderBy.equals(query.orderBy) : query.orderBy != null) return false;
        return !(limit != null ? !limit.equals(query.limit) : query.limit != null);

    }

    @Override
    public int hashCode() {
        int result = (distinct ? 1 : 0);
        result = 31 * result + table.hashCode();
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? whereArgs.hashCode() : 0);
        result = 31 * result + (groupBy != null ? groupBy.hashCode() : 0);
        result = 31 * result + (having != null ? having.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Query{" +
                "distinct=" + distinct +
                ", table='" + table + '\'' +
                ", columns=" + columns +
                ", where='" + where + '\'' +
                ", whereArgs=" + whereArgs +
                ", groupBy='" + groupBy + '\'' +
                ", having='" + having + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", limit='" + limit + '\'' +
                '}';
    }

    /**
     * Builder for {@link Query}
     */
    public static class Builder {

        private boolean distinct;
        private String table;
        private List<String> columns;
        private String where;
        private List<String> whereArgs;
        private String groupBy;
        private String having;
        private String orderBy;
        private String limit;

        /**
         * Optional: Specifies distinct option
         * <p/>
         * True if you want each row to be unique, false otherwise
         * <p/>
         * Default value is <code>false</code>
         *
         * @param distinct distinct option
         * @return builder
         */
        @NonNull
        public Builder distinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }

        /**
         * Required: Specifies table name
         * <p/>
         * Default value is <code>null</code>
         *
         * @param table table name
         * @return builder
         */
        @NonNull
        public Builder table(@NonNull String table) {
            this.table = table;
            return this;
        }

        /**
         * Optional: Specifies list of columns that should be received.
         * <p/>
         * If list will be null or empty -> all columns will be received
         * <p/>
         * Default value is <code>null</code>
         *
         * @param columns list of columns to receive
         * @return builder
         */
        @NonNull
        public Builder columns(@Nullable String... columns) {
            this.columns = QueryUtil.varargsToList(columns);
            return this;
        }

        /**
         * Optional: Specifies where clause
         * <p/>
         * Optional filter declaring which rows to return
         * <p/>
         * Formatted as an SQL WHERE clause (excluding the WHERE itself).
         * <p/>
         * Passing null will RETURN all rows for the given table
         * <p/>
         * Default value is <code>null</code>
         *
         * @param where where clause
         * @return builder
         */
        @NonNull
        public Builder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for where clause
         * <p/>
         * Passed objects will be immediately converted list of to {@link String} via calling {@link Object#toString()}
         * <p/>
         * Default value is <code>null</code>
         *
         * @param whereArgs list of arguments for where clause
         * @return builder
         */
        @NonNull
        public Builder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = QueryUtil.varargsToList(whereArgs);
            return this;
        }

        /**
         * Optional: Specifies group by clause
         * <p/>
         * Optional filter declaring how to group rows.
         * <p/>
         * Formatted as an SQL GROUP BY clause (excluding the GROUP BY itself).
         * <p/>
         * Passing null will cause the rows to not be grouped
         * <p/>
         * Default value is <code>null</code>
         *
         * @param groupBy group by clause
         * @return builder
         */
        @NonNull
        public Builder groupBy(@Nullable String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        /**
         * Optional: Specifies having clause
         * <p/>
         * Optional filter declare which row groups to include in the cursor, if row grouping is being used.
         * <p/>
         * Formatted as an SQL HAVING clause (excluding the HAVING itself).
         * <p/>
         * Passing null will cause all row groups to be included, and is required when row grouping is not being used
         * <p/>
         * Default value is <code>null</code>
         *
         * @param having having clause
         * @return builder
         */
        @NonNull
        public Builder having(@Nullable String having) {
            this.having = having;
            return this;
        }

        /**
         * Optional: Specifies order by clause
         * <p/>
         * Optional specifier to how to order the rows.
         * <p/>
         * Formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
         * <p/>
         * Passing null will use the default sort order, which may be unordered
         * <p/>
         * Default value is <code>null</code>
         *
         * @param orderBy order by clause
         * @return builder
         */
        @NonNull
        public Builder orderBy(@Nullable String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        /**
         * Optional: Specifies limit clause
         * <p/>
         * Optional specifier that limits the number of rows returned by the query.
         * <p/>
         * Formatted as LIMIT clause.
         * <p/>
         * Passing null denotes no LIMIT clause
         * <p/>
         * Default value is <code>null</code>
         *
         * @param limit limit clause
         * @return builder
         */
        @NonNull
        public Builder limit(@Nullable String limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Builds immutable instance of {@link Query}
         *
         * @return immutable instance of {@link Query}
         */
        @NonNull
        public Query build() {
            Checks.checkNotEmpty(table, "Please specify table name");

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
