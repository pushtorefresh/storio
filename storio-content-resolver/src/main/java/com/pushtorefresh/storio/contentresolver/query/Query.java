package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.Queries;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 * <p>
 * Instances of this class are Immutable.
 */
public final class Query {

    @NonNull
    private final Uri uri;

    @Nullable
    private final List<String> columns;

    @Nullable
    private final String where;

    @Nullable
    private final List<String> whereArgs;

    @Nullable
    private final String sortOrder;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.query.Query.Builder}
     * instead of constructor.
     */
    private Query(@NonNull Uri uri, @Nullable List<String> columns, @Nullable String where, @Nullable List<String> whereArgs, @Nullable String sortOrder) {
        this.uri = uri;
        this.columns = Queries.listToUnmodifiable(columns);
        this.where = where;
        this.whereArgs = Queries.listToUnmodifiable(whereArgs);
        this.sortOrder = sortOrder;
    }

    /**
     * Gets URI to query.
     * <p>
     * This will be the full URI sent by the client.
     * If the client is requesting a specific record, the URI will
     * end in a record number that the implementation should parse and
     * add to a {@code WHERE} or {@code HAVING} clause,
     * specifying that {@code _id} value.
     *
     * @return non-null URI to query.
     */
    @NonNull
    public Uri uri() {
        return uri;
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
     * If it's {@code null} — Query will retrieve all rows for specified URI.
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
     * Gets sort order.
     * <p>
     * How the rows in the cursor should be sorted.
     * If {@code null} then the provider is free to define the sort order.
     *
     * @return nullable sort order.
     */
    @Nullable
    public String sortOrder() {
        return sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        if (!uri.equals(query.uri)) return false;
        if (columns != null ? !columns.equals(query.columns) : query.columns != null)
            return false;
        if (where != null ? !where.equals(query.where) : query.where != null)
            return false;
        if (whereArgs != null ? !whereArgs.equals(query.whereArgs) : query.whereArgs != null)
            return false;
        return !(sortOrder != null ? !sortOrder.equals(query.sortOrder) : query.sortOrder != null);
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? whereArgs.hashCode() : 0);
        result = 31 * result + (sortOrder != null ? sortOrder.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Query{" +
                "uri=" + uri +
                ", columns=" + columns +
                ", where='" + where + '\'' +
                ", whereArgs=" + whereArgs +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }

    /**
     * Builder for {@link Query}.
     */
    public static final class Builder {

        /**
         * Required: Specifies URI to query.
         * This will be the full URI sent by the client;
         * if the client is requesting a specific record,
         * the URI will end in a record number that the implementation should parse and add to a WHERE or HAVING clause,
         * specifying that _id value.
         *
         * @param uri uri.
         * @return builder.
         * @see Query#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull Uri uri) {
            checkNotNull(uri, "Please specify uri");
            return new CompleteBuilder(uri);
        }

        /**
         * Required: Specifies URI to query.
         * This will be the full URI sent by the client;
         * if the client is requesting a specific record,
         * the URI will end in a record number that the implementation should parse and add to a WHERE or HAVING clause,
         * specifying that _id value.
         *
         * @param uri uri string which will be converted to {@link Uri}.
         * @return builder.
         * @see Query#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull String uri) {
            return new CompleteBuilder(Uri.parse(uri));
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private final Uri uri;

        private List<String> columns;

        private String where;

        private List<String> whereArgs;

        private String sortOrder;

        CompleteBuilder(@NonNull Uri uri) {
            this.uri = uri;
        }

        /**
         * Optional: The list of columns to put into the cursor.
         * If {@code null} all columns are included.
         * <p>
         * Default value is {@code null}.
         *
         * @param columns columns.
         * @return builder.
         * @see Query#columns()
         */
        @NonNull
        public CompleteBuilder columns(@Nullable String... columns) {
            this.columns = Queries.varargsToList(columns);
            return this;
        }

        /**
         * Optional: A selection criteria to apply when filtering rows.
         * If {@code null} then all rows are included.
         * <p>
         * Default value is {@code null}.
         *
         * @param where where clause.
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
         * Passed objects will be immediately converted
         * to list {@link String} via calling {@link Object#toString()}.
         * <p>
         * Default value is {@code null}.
         *
         * @param whereArgs selection arguments.
         * @return builder.
         * @see Query#whereArgs()
         */
        @NonNull
        public CompleteBuilder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = Queries.varargsToList(whereArgs);
            return this;
        }

        /**
         * Optional: Specifies how the rows in the cursor should be sorted.
         * If {@code null} then the provider is free to define the sort order.
         * <p>
         * Default value is {@code null}.
         *
         * @param sortOrder sort order.
         * @return builder.
         * @see Query#sortOrder()
         */
        @NonNull
        public CompleteBuilder sortOrder(@Nullable String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        /**
         * Builds new instance of {@link Query}.
         *
         * @return new instance of {@link Query}.
         */
        @NonNull
        public Query build() {
            return new Query(
                    uri,
                    columns,
                    where,
                    whereArgs,
                    sortOrder
            );
        }
    }
}
