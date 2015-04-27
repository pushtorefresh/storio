package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.QueryUtil;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}
 * <p/>
 * Instances of this class are Immutable
 */
public final class Query {

    /**
     * The URI to query. This will be the full URI sent by the client;
     * if the client is requesting a specific record, the URI will
     * end in a record number that the implementation should parse and
     * add to a WHERE or HAVING clause, specifying that _id value.
     */
    @NonNull
    public final Uri uri;

    /**
     * The list of columns to put into the cursor. If null all columns are included.
     */
    @Nullable
    public final List<String> projection;

    /**
     * A selection criteria to apply when filtering rows. If null then all rows are included.
     */
    @Nullable
    public final String where;

    /**
     * You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
     */
    @Nullable
    public final List<String> whereArgs;

    /**
     * How the rows in the cursor should be sorted. If null then the provider is free to define the sort order.
     */
    @Nullable
    public final String sortOrder;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.query.Query.Builder} instead of constructor
     */
    protected Query(@NonNull Uri uri, @Nullable List<String> projection, @Nullable String where, @Nullable List<String> whereArgs, @Nullable String sortOrder) {
        this.uri = uri;
        this.projection = QueryUtil.listToUnmodifiable(projection);
        this.where = where;
        this.whereArgs = QueryUtil.listToUnmodifiable(whereArgs);
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        if (!uri.equals(query.uri)) return false;
        if (projection != null ? !projection.equals(query.projection) : query.projection != null)
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
        result = 31 * result + (projection != null ? projection.hashCode() : 0);
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? whereArgs.hashCode() : 0);
        result = 31 * result + (sortOrder != null ? sortOrder.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Query{" +
                "uri=" + uri +
                ", projection=" + projection +
                ", where='" + where + '\'' +
                ", whereArgs=" + whereArgs +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }

    /**
     * Builder for {@link Query}
     */
    public static final class Builder {

        /**
         * Specifies URI to query.
         * This will be the full URI sent by the client;
         * if the client is requesting a specific record,
         * the URI will end in a record number that the implementation should parse and add to a WHERE or HAVING clause,
         * specifying that _id value.
         *
         * @param uri uri
         * @return builder
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
         * @param uri uri which will be converted to {@link Uri}
         * @return builder
         */
        @NonNull
        public CompleteBuilder uri(@NonNull String uri) {
            return new CompleteBuilder(Uri.parse(uri));
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}
     */
    public static final class CompleteBuilder {

        @NonNull
        private final Uri uri;

        private List<String> projection;

        private String where;

        private List<String> whereArgs;

        private String sortOrder;

        CompleteBuilder(@NonNull Uri uri) {
            this.uri = uri;
        }

        /**
         * The list of columns to put into the cursor.
         * If <code>null</code> all columns are included.
         * <p/>
         * Default value is <code>null</code>
         *
         * @param columns columns
         * @return builder
         */
        @NonNull
        public CompleteBuilder projection(@Nullable String... columns) {
            projection = QueryUtil.varargsToList(columns);
            return this;
        }

        /**
         * Optional: A selection criteria to apply when filtering rows.
         * If <code>null</code> then all rows are included.
         * <p/>
         * Default value is <code>null</code>
         *
         * @param where where
         * @return builder
         */
        @NonNull
        public CompleteBuilder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for where clause
         * <p/>
         * Passed objects will be immediately converted to list {@link String} via calling {@link Object#toString()}
         * <p/>
         * Default value is <code>null</code>
         *
         * @param whereArgs selection arguments
         * @return builder
         */
        @NonNull
        public CompleteBuilder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = QueryUtil.varargsToList(whereArgs);
            return this;
        }

        /**
         * Optional: Specifies how the rows in the cursor should be sorted.
         * If <code>null</code> then the provider is free to define the sort order.
         * <p/>
         * Default value is <code>null</code>
         *
         * @param sortOrder sort order
         * @return builder
         */
        @NonNull
        public CompleteBuilder sortOrder(@Nullable String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        /**
         * Builds new instance of {@link Query}
         *
         * @return new instance of {@link Query}
         */
        @NonNull
        public Query build() {
            return new Query(
                    uri,
                    projection,
                    where,
                    whereArgs,
                    sortOrder
            );
        }
    }
}
