package com.pushtorefresh.storio.contentprovider.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.util.QueryUtil;

import java.util.List;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Query for {@link com.pushtorefresh.storio.contentprovider.StorIOContentProvider}
 * <p/>
 * Instances of this class are Immutable
 */
public class Query {

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
     * Please use {@link com.pushtorefresh.storio.contentprovider.query.Query.Builder} instead of constructor
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
    public static class Builder {

        private Uri uri;
        private List<String> projection;
        private String where;
        private List<String> whereArgs;
        private String sortOrder;

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
        public Builder uri(@NonNull Uri uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Specifies URI to query.
         * This will be the full URI sent by the client;
         * if the client is requesting a specific record,
         * the URI will end in a record number that the implementation should parse and add to a WHERE or HAVING clause,
         * specifying that _id value.
         *
         * @param uri uri which will be converted to {@link Uri}
         * @return builder
         */
        @NonNull
        public Builder uri(@NonNull String uri) {
            this.uri = Uri.parse(uri);
            return this;
        }

        /**
         * The list of columns to put into the cursor. If null all columns are included.
         *
         * @param columns columns
         * @return builder
         */
        @NonNull
        public Builder projection(@Nullable String... columns) {
            projection = QueryUtil.varargsToList(columns);
            return this;
        }

        /**
         * A selection criteria to apply when filtering rows. If null then all rows are included.
         *
         * @param where where
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
         * Passed objects will be immediately converted to list {@link String} via calling {@link Object#toString()}
         * <p/>
         * Default value is <code>null</code>
         *
         * @param whereArgs selection arguments
         * @return builder
         */
        @NonNull
        public Builder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = QueryUtil.varargsToList(whereArgs);
            return this;
        }

        /**
         * How the rows in the cursor should be sorted. If null then the provider is free to define the sort order.
         *
         * @param sortOrder sort order
         * @return builder
         */
        @NonNull
        public Builder sortOrder(@Nullable String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        /**
         * Builds new instance of {@link Query},
         * can throw {@link IllegalStateException} if something is incorrect
         *
         * @return new instance of {@link Query}
         */
        @NonNull
        public Query build() {
            checkNotNull(uri, "Please specify uri");

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
