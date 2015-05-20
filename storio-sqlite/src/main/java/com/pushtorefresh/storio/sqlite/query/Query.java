package com.pushtorefresh.storio.sqlite.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.Queries;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;

/**
 * Get query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}.
 * <p>
 * Instances of this class are immutable.
 */
public final class Query {

    private final boolean distinct;

    @NonNull
    private final String table;

    @Nullable
    private final List<String> columns;

    @Nullable
    private final String where;

    @Nullable
    private final List<String> whereArgs;

    @Nullable
    private final String groupBy;

    @Nullable
    private final String having;

    @Nullable
    private final String orderBy;

    @Nullable
    private final String limit;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlite.query.Query.Builder}
     * instead of constructor.
     */
    private Query(boolean distinct, @NonNull String table, @Nullable List<String> columns,
                  @Nullable String where, @Nullable List<String> whereArgs,
                  @Nullable String groupBy, @Nullable String having,
                  @Nullable String orderBy, @Nullable String limit) {
        this.distinct = distinct;
        this.table = table;
        this.columns = Queries.unmodifiableNullableList(columns);
        this.where = where;
        this.whereArgs = Queries.unmodifiableNullableList(whereArgs);
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.limit = limit;
    }

    /**
     * Gets distinct option.
     * <p>
     * True if you want each row to be unique, false otherwise.
     *
     * @return distinct option.
     */
    public boolean distinct() {
        return distinct;
    }

    /**
     * Gets table name.
     *
     * @return non-null table name.
     */
    @NonNull
    public String table() {
        return table;
    }

    /**
     * Gets optional immutable list of columns that should be received.
     * <p>
     * If list is {@code null} or empty -> all columns will be received.
     *
     * @return immutable list of columns that should be received.
     */
    @Nullable
    public List<String> columns() {
        return columns;
    }

    /**
     * Gets {@code WHERE} clause.
     * <p>
     * Optional filter declaring which rows to return.
     * <p>
     * Formatted as an SQL {@code WHERE} clause (excluding the {@code WHERE} itself).
     * <p>
     * If it's {@code null} — Query will retrieve all rows for the given table.
     *
     * @return nullable {@code WHERE} clause.
     */
    @Nullable
    public String where() {
        return where;
    }

    /**
     * Gets optional immutable list of arguments for {@link #where()} clause.
     *
     * @return nullable immutable list of arguments for {@code WHERE} clause.
     */
    @Nullable
    public List<String> whereArgs() {
        return whereArgs;
    }

    /**
     * Gets {@code GROUP BY} clause.
     * <p>
     * Optional filter declaring how to group rows.
     * <p>
     * Formatted as an SQL {@code GROUP BY} clause (excluding the {@code GROUP BY} itself).
     * <p>
     * Passing {@code null} will cause the rows to not be grouped.
     *
     * @return nullable {@code GROUP BY} clause.
     */
    @Nullable
    public String groupBy() {
        return groupBy;
    }

    /**
     * Gets having clause.
     * <p>
     * Optional filter declare which row groups to include
     * in the cursor, if row grouping is being used.
     * <p>
     * Formatted as an SQL HAVING clause (excluding the HAVING itself).
     * <p>
     * Passing {@code null} will cause all row groups to be included,
     * and is required when row grouping is not being used.
     *
     * @return nullable {@code HAVING} clause.
     */
    @Nullable
    public String having() {
        return having;
    }

    /**
     * Gets {@code ORDER BY} clause.
     * <p>
     * Optional specifier to how to order the rows.
     * <p>
     * Formatted as an SQL {@code ORDER BY} clause (excluding the {@code ORDER BY} itself).
     * <p>
     * Passing {@code null} will use the default sort order, which may be unordered.
     *
     * @return nullable {@code ORDER BY} clause.
     */
    @Nullable
    public String orderBy() {
        return orderBy;
    }

    /**
     * Gets {@code LIMIT} clause.
     * <p>
     * Optional specifier that limits the number of rows returned by the query.
     * <p>
     * Formatted as {@code LIMIT} clause.
     * <p>
     * Passing {@code null} denotes no {@code LIMIT} clause.
     *
     * @return nullable {@code LIMIT} clause.
     */
    @Nullable
    public String limit() {
        return limit;
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
     * Builder for {@link Query}.
     */
    public static final class Builder {

        /**
         * Required: Specifies table name.
         *
         * @param table non-null and not empty table name.
         * @return builder.
         * @see Query#table()
         */
        @NonNull
        public CompleteBuilder table(@NonNull String table) {
            checkNotEmpty(table, "Table name is null or empty");
            return new CompleteBuilder(table);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private final String table;

        private boolean distinct;

        private List<String> columns;

        private String where;

        private List<String> whereArgs;

        private String groupBy;

        private String having;

        private String orderBy;

        private String limit;

        CompleteBuilder(@NonNull String table) {
            this.table = table;
        }

        /**
         * Optional: Specifies distinct option.
         * <p>
         * Set {@code true} if you want each row to be unique, {@code false} otherwise.
         * <p>
         * Default value is {@code false}.
         *
         * @param distinct distinct option.
         * @return builder.
         * @see Query#distinct()
         */
        @NonNull
        public CompleteBuilder distinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }

        /**
         * Optional: Specifies list of columns that should be received.
         * <p>
         * If list will be {@code null} or empty -> all columns will be received.
         * <p>
         * Default value is {@code null}.
         *
         * @param columns list of columns to receive.
         * @return builder.
         * @see Query#columns()
         */
        @NonNull
        public CompleteBuilder columns(@Nullable String... columns) {
            this.columns = Queries.varargsToList(columns);
            return this;
        }

        /**
         * Optional: Specifies {@code WHERE} clause.
         * <p>
         * Optional filter declaring which rows to return.
         * <p>
         * Formatted as an SQL {@code WHERE} clause (excluding the {@code WHERE} itself).
         * <p>
         * Passing {@code null} will RETURN all rows for the given table.
         * <p>
         * Default value is {@code null}.
         *
         * @param where {@code WHERE} clause.
         * @return builder.
         * @see Query#where()
         */
        @NonNull
        public CompleteBuilder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for where clause.
         * <p>
         * Passed objects will be immediately converted list of
         * to {@link String} via calling {@link Object#toString()}.
         * <p>
         * Default value is {@code null}.
         *
         * @param whereArgs list of arguments for where clause.
         * @return builder.
         * @see Query#whereArgs()
         */
        @NonNull
        public CompleteBuilder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = Queries.varargsToList(whereArgs);
            return this;
        }

        /**
         * Optional: Specifies {@code GROUP BY} clause.
         * <p>
         * Optional filter declaring how to group rows.
         * <p>
         * Formatted as an SQL {@code GROUP BY} clause (excluding the {@code GROUP BY} itself).
         * <p>
         * Passing {@code null} will cause the rows to not be grouped.
         * <p>
         * Default value is {@code null}.
         *
         * @param groupBy {@code GROUP BY} clause.
         * @return builder.
         * @see Query#groupBy()
         */
        @NonNull
        public CompleteBuilder groupBy(@Nullable String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        /**
         * Optional: Specifies {@code HAVING} clause.
         * <p>
         * Optional filter declare which row groups to include in the cursor,
         * if row grouping is being used.
         * <p>
         * Formatted as an SQL {@code HAVING} clause (excluding the {@code HAVING} itself).
         * <p>
         * Passing {@code null} will cause all row groups to be included,
         * and is required when row grouping is not being used.
         * <p>
         * Default value is {@code null}.
         *
         * @param having {@code HAVING} clause.
         * @return builder.
         * @see Query#having()
         */
        @NonNull
        public CompleteBuilder having(@Nullable String having) {
            this.having = having;
            return this;
        }

        /**
         * Optional: Specifies {@code ORDER BY} clause.
         * <p>
         * Optional specifier to how to order the rows.
         * <p>
         * Formatted as an SQL {@code ORDER BY} clause (excluding the {@code ORDER BY} itself).
         * <p>
         * Passing {@code null} will use the default sort order, which may be unordered.
         * <p>
         * Default value is {@code null}.
         *
         * @param orderBy {@code ORDER BY} clause.
         * @return builder.
         * @see Query#orderBy()
         */
        @NonNull
        public CompleteBuilder orderBy(@Nullable String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        /**
         * Optional: Specifies {@code LIMIT} clause.
         * <p>
         * Optional specifier that limits the number of rows returned by the query.
         * <p>
         * Formatted as {@code LIMIT} clause.
         * <p>
         * Passing {@code null} denotes no {@code LIMIT} clause.
         * <p>
         * Default value is {@code null}.
         *
         * @param limit {@code LIMIT} clause.
         * @return builder.
         * @see Query#limit()
         */
        @NonNull
        public CompleteBuilder limit(@Nullable String limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Builds immutable instance of {@link Query}.
         *
         * @return immutable instance of {@link Query}.
         */
        @NonNull
        public Query build() {
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
