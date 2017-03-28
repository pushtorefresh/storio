package com.pushtorefresh.storio.sqlite.queries;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.internal.InternalQueries.nonNullSet;
import static com.pushtorefresh.storio.internal.InternalQueries.nonNullString;
import static com.pushtorefresh.storio.internal.InternalQueries.unmodifiableNonNullListOfStrings;
import static com.pushtorefresh.storio.internal.InternalQueries.unmodifiableNonNullSet;

/**
 * Get query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}.
 * <p>
 * Instances of this class are immutable.
 */
public final class Query {

    private final boolean distinct;

    @NonNull
    private final String table;

    @NonNull
    private final List<String> columns;

    @NonNull
    private final String where;

    @NonNull
    private final List<String> whereArgs;

    @NonNull
    private final String groupBy;

    @NonNull
    private final String having;

    @NonNull
    private final String orderBy;

    @NonNull
    private final String limit;

    @NonNull
    private final Set<String> observesTags;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlite.queries.Query.Builder}
     * instead of constructor.
     */
    private Query(boolean distinct, @NonNull String table, @Nullable List<String> columns,
                  @Nullable String where, @Nullable List<String> whereArgs,
                  @Nullable String groupBy, @Nullable String having,
                  @Nullable String orderBy, @Nullable String limit, @Nullable Set<String> observesTags) {

        if (observesTags != null) {
            for (String tag : observesTags) {
                checkNotEmpty(tag, "observesTag must not be null or empty, observesTags = " + observesTags);
            }
        }

        this.distinct = distinct;
        this.table = table;
        this.columns = unmodifiableNonNullListOfStrings(columns);
        this.where = nonNullString(where);
        this.whereArgs = unmodifiableNonNullListOfStrings(whereArgs);
        this.groupBy = nonNullString(groupBy);
        this.having = nonNullString(having);
        this.orderBy = nonNullString(orderBy);
        this.limit = nonNullString(limit);
        this.observesTags = unmodifiableNonNullSet(observesTags);
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
     * If list is empty — all columns will be received.
     *
     * @return non-null, immutable list of columns that should be received.
     */
    @NonNull
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
     * If empty — Query will retrieve all rows for the given table.
     *
     * @return non-null {@code WHERE} clause.
     */
    @NonNull
    public String where() {
        return where;
    }

    /**
     * Gets optional immutable list of arguments for {@link #where()} clause.
     *
     * @return non-null, immutable list of arguments for {@code WHERE} clause.
     */
    @NonNull
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
     * Passing {@code null} or empty string will cause the rows to not be grouped.
     *
     * @return non-null {@code GROUP BY} clause.
     */
    @NonNull
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
     * Passing {@code null} or empty string will cause all row groups to be included,
     * and is required when row grouping is not being used.
     *
     * @return non-null {@code HAVING} clause.
     */
    @NonNull
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
     * Passing {@code null} or empty string will use the default sort order, which may be unordered.
     *
     * @return non-null {@code ORDER BY} clause.
     */
    @NonNull
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
     * Passing {@code null} or empty String denotes no {@code LIMIT} clause.
     *
     * @return non-null {@code LIMIT} clause.
     */
    @NonNull
    public String limit() {
        return limit;
    }

    /**
     * Gets optional immutable set of tags that should be observed by this query.
     * <p>
     * It will be used to observe changes of this tags and re-execute this query.
     *
     * @return non-null, immutable set of tags, that should be observed by this query.
     */
    @NonNull
    public Set<String> observesTags() {
        return observesTags;
    }

    /**
     * Returns the new builder that has the same content as this query.
     * It can be used to create new queries.
     *
     * @return non-null new instance of {@link CompleteBuilder} with content of this query.
     */
    @NonNull
    public CompleteBuilder toBuilder() {
        return new CompleteBuilder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        if (distinct != query.distinct) return false;
        if (!table.equals(query.table)) return false;
        if (!columns.equals(query.columns)) return false;
        if (!where.equals(query.where)) return false;
        if (!whereArgs.equals(query.whereArgs)) return false;
        if (!groupBy.equals(query.groupBy)) return false;
        if (!having.equals(query.having)) return false;
        if (!orderBy.equals(query.orderBy)) return false;
        if (!limit.equals(query.limit)) return false;
        return observesTags.equals(query.observesTags);
    }

    @Override
    public int hashCode() {
        int result = (distinct ? 1 : 0);
        result = 31 * result + table.hashCode();
        result = 31 * result + columns.hashCode();
        result = 31 * result + where.hashCode();
        result = 31 * result + whereArgs.hashCode();
        result = 31 * result + groupBy.hashCode();
        result = 31 * result + having.hashCode();
        result = 31 * result + orderBy.hashCode();
        result = 31 * result + limit.hashCode();
        result = 31 * result + observesTags.hashCode();
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
                ", observesTags='" + observesTags + '\'' +
                '}';
    }

    /**
     * Creates new builder for {@link Query}.
     *
     * @return non-null instance of {@link Query.Builder}.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link Query}.
     */
    public static final class Builder {

        /**
         * Please use {@link Query#builder()} instead of this.
         */
        Builder() {
        }

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
        private String table;

        private boolean distinct;

        private List<String> columns;

        private String where;

        private List<String> whereArgs;

        private String groupBy;

        private String having;

        private String orderBy;

        private String limit;

        @Nullable
        private Set<String> observesTags;

        CompleteBuilder(@NonNull String table) {
            this.table = table;
        }

        CompleteBuilder(@NonNull Query query) {
            this.table = query.table;
            this.distinct = query.distinct;
            this.columns = query.columns;
            this.where = query.where;
            this.whereArgs = query.whereArgs;
            this.groupBy = query.groupBy;
            this.having = query.having;
            this.orderBy = query.orderBy;
            this.limit = query.limit;
            this.observesTags = query.observesTags;
        }

        /**
         * Specifies table name.
         *
         * @param table non-null and not empty table name.
         * @return builder.
         * @see Query#table()
         */
        @NonNull
        public CompleteBuilder table(@NonNull String table) {
            checkNotEmpty(table, "Table name is null or empty");
            this.table = table;
            return this;
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
         * If list will be {@code null} or empty — all columns will be received.
         * <p>
         * Default value is {@code null}.
         *
         * @param columns list of columns to receive.
         * @return builder.
         * @see Query#columns()
         */
        @NonNull
        public CompleteBuilder columns(@Nullable String... columns) {
            this.columns = unmodifiableNonNullListOfStrings(columns);
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
        public <T> CompleteBuilder whereArgs(@Nullable T... whereArgs) {
            this.whereArgs = unmodifiableNonNullListOfStrings(whereArgs);
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
        public CompleteBuilder whereArgs(@Nullable List<?> whereArgs) {
            this.whereArgs = unmodifiableNonNullListOfStrings(whereArgs);
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
         * Formatted as {@code LIMIT} clause: {@code "[offset], rows"}. Examples:
         * <ul>
         * <li>{@code "5"} — will limit output to first five rows.</li>
         * <li>{@code "5, 12"} — will limit output to 12 rows with start offset {@code == 5}.</li>
         * <p>
         * Passing {@code null} denotes no {@code LIMIT} clause.
         * <p>
         * Default value is {@code null}.
         *
         * @param limit {@code LIMIT} clause.
         * @return builder.
         * @see Query#limit()
         * @see CompleteBuilder#limit(int)
         * @see CompleteBuilder#limit(int, int)
         * @see <a href="https://www.sqlite.org/lang_select.html#limitoffset">The LIMIT clause documentation</a>
         */
        @NonNull
        public CompleteBuilder limit(@Nullable String limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Optional: Specifies {@code LIMIT} clause.
         * <p>
         * Optional specifier that limits the number of rows returned by the query.
         *
         * @param limit positive number of rows returned by the query.
         * @return builder.
         * @see Query#limit()
         * @see CompleteBuilder#limit(String)
         * @see CompleteBuilder#limit(int, int)
         * @see <a href="https://www.sqlite.org/lang_select.html#limitoffset">The LIMIT clause documentation</a>
         */
        @NonNull
        public CompleteBuilder limit(final int limit) {
            if (limit <= 0) {
                throw new IllegalStateException("Parameter `limit` should be positive, but was = " + limit);
            }
            this.limit = String.valueOf(limit);
            return this;
        }

        /**
         * Optional: Specifies {@code LIMIT} clause.
         * <p>
         * Optional specifier that limits the number of rows returned by the query.
         * <p>
         * Examples:
         * <ul>
         * <li>{@code "offset = 5, quantity = 12"} — will limit output to 12 rows with start offset 5.</li>
         *
         * @param offset non-negative start position.
         * @param quantity positive number of queried rows.
         * @return builder.
         * @see Query#limit()
         * @see CompleteBuilder#limit(String)
         * @see CompleteBuilder#limit(int)
         * @see <a href="https://www.sqlite.org/lang_select.html#limitoffset">The LIMIT clause documentation</a>
         */
        @NonNull
        public CompleteBuilder limit(final int offset, final int quantity) {
            if (offset < 0) {
                throw new IllegalStateException("Parameter `offset` should not be negative, but was = " + offset);
            }
            if (quantity <= 0) {
                throw new IllegalStateException("Parameter `quantity` should be positive, but was = " + quantity);
            }
            this.limit = String.valueOf(offset) + ", " + String.valueOf(quantity);
            return this;
        }

        /**
         * Optional: Specifies set of tags which should be observed by this query.
         * <p/>
         * It will be used to observe changes of tags and re-execute this query.
         *
         * @param tag the first required tag which will be observed by this query.
         * @param tags optional set of tags which will be observed by this query.
         * @return builder.
         * @see Query#observesTags()
         * @see com.pushtorefresh.storio.sqlite.StorIOSQLite#observeChangesOfTag(String)
         */
        @NonNull
        public CompleteBuilder observesTags(@NonNull String tag, @Nullable String... tags) {
            observesTags = nonNullSet(tag, tags);
            return this;
        }

        /**
         * Optional: Specifies set of tags which should be observed by this query.
         * <p/>
         * It will be used to observe changes of tags and re-execute this query.
         *
         * @param tags set of tags which will be observed by this query.
         * @return builder.
         * @see Query#observesTags()
         * @see com.pushtorefresh.storio.sqlite.StorIOSQLite#observeChangesOfTag(String)
         */
        @NonNull
        public CompleteBuilder observesTags(@Nullable Collection<String> tags) {
            observesTags = nonNullSet(tags);
            return this;
        }

        /**
         * Builds immutable instance of {@link Query}.
         *
         * @return immutable instance of {@link Query}.
         */
        @NonNull
        public Query build() {
            if (where == null && whereArgs != null && !whereArgs.isEmpty()) {
                throw new IllegalStateException("You can not use whereArgs without where clause");
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
                    limit,
                    observesTags
            );
        }
    }
}
