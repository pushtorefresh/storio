package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.InternalQueries.nonNullString;
import static com.pushtorefresh.storio.internal.InternalQueries.unmodifiableNonNullList;
import static com.pushtorefresh.storio.internal.InternalQueries.unmodifiableNonNullListOfStrings;

/**
 * Query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 * <p>
 * Instances of this class are Immutable.
 */
public final class Query {

    @NonNull
    private final Uri uri;

    @NonNull
    private final List<String> columns;

    @NonNull
    private final String where;

    @NonNull
    private final List<String> whereArgs;

    @NonNull
    private final String sortOrder;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.queries.Query.Builder}
     * instead of constructor.
     */
    private Query(@NonNull Uri uri, @Nullable List<String> columns, @Nullable String where, @Nullable List<String> whereArgs, @Nullable String sortOrder) {
        this.uri = uri;
        this.columns = unmodifiableNonNullList(columns);
        this.where = nonNullString(where);
        this.whereArgs = unmodifiableNonNullList(whereArgs);
        this.sortOrder = nonNullString(sortOrder);
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
     * If it's empty — Query will retrieve all rows for specified URI.
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
     * Gets sort order.
     * <p>
     * How the rows in the cursor should be sorted.
     * If empty then the provider is free to define the sort order.
     *
     * @return non-null sort order.
     */
    @NonNull
    public String sortOrder() {
        return sortOrder;
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

        if (!uri.equals(query.uri)) return false;
        if (!columns.equals(query.columns)) return false;
        if (!where.equals(query.where)) return false;
        if (!whereArgs.equals(query.whereArgs)) return false;
        return sortOrder.equals(query.sortOrder);
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + columns.hashCode();
        result = 31 * result + where.hashCode();
        result = 31 * result + whereArgs.hashCode();
        result = 31 * result + sortOrder.hashCode();
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
            checkNotEmpty(uri, "Uri should not be null");
            return new CompleteBuilder(Uri.parse(uri));
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private Uri uri;

        private List<String> columns;

        private String where;

        private List<String> whereArgs;

        private String sortOrder;

        CompleteBuilder(@NonNull Uri uri) {
            this.uri = uri;
        }

        CompleteBuilder(@NonNull Query query) {
            this.uri = query.uri;
            this.columns = query.columns;
            this.where = query.where;
            this.whereArgs = query.whereArgs;
            this.sortOrder = query.sortOrder;
        }

        /**
         * Specifies uri.
         *
         * @param uri full {@code URI} to query, including a row ID
         *            (if a specific record is requested).
         * @return builder.
         * @see Query#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull Uri uri) {
            checkNotNull(uri, "Please specify uri");
            this.uri = uri;
            return this;
        }

        /**
         * Specifies uri.
         *
         * @param uri full {@code URI} to query,
         *            including a row ID (if a specific record is requested).
         * @return builder.
         * @see Query#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull String uri) {
            checkNotEmpty(uri, "Uri should not be null");
            this.uri = Uri.parse(uri);
            return this;
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
            this.columns = unmodifiableNonNullListOfStrings(columns);
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
        public <T> CompleteBuilder whereArgs(@Nullable T... whereArgs) {
            this.whereArgs = unmodifiableNonNullListOfStrings(whereArgs);
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
