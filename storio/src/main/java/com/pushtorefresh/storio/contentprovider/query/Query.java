package com.pushtorefresh.storio.contentprovider.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Query {

    /**
     * The URI to query. This will be the full URI sent by the client;
     * if the client is requesting a specific record, the URI will
     * end in a record number that the implementation should parse and
     * add to a WHERE or HAVING clause, specifying that _id value.
     */
    @NonNull public final Uri uri;

    /**
     * The list of columns to put into the cursor. If null all columns are included.
     */
    @Nullable public final List<String> projection;

    /**
     * A selection criteria to apply when filtering rows. If null then all rows are included.
     */
    @Nullable public final String selection;

    /**
     * You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
     */
    @Nullable public final List<String> selectionArgs;

    /**
     * How the rows in the cursor should be sorted. If null then the provider is free to define the sort order.
     */
    @Nullable public final String sortOrder;

    public Query(@NonNull Uri uri, @Nullable List<String> projection, @Nullable String selection, @Nullable List<String> selectionArgs, @Nullable String sortOrder) {
        this.uri = uri;
        this.projection = projection != null ? Collections.unmodifiableList(projection) : null;
        this.selection = selection;
        this.selectionArgs = selectionArgs != null ? Collections.unmodifiableList(selectionArgs) : null;
        this.sortOrder = sortOrder;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        if (!uri.equals(query.uri)) return false;
        if (projection != null ? !projection.equals(query.projection) : query.projection != null)
            return false;
        if (selection != null ? !selection.equals(query.selection) : query.selection != null)
            return false;
        if (selectionArgs != null ? !selectionArgs.equals(query.selectionArgs) : query.selectionArgs != null)
            return false;
        return !(sortOrder != null ? !sortOrder.equals(query.sortOrder) : query.sortOrder != null);

    }

    @Override public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (projection != null ? projection.hashCode() : 0);
        result = 31 * result + (selection != null ? selection.hashCode() : 0);
        result = 31 * result + (selectionArgs != null ? selectionArgs.hashCode() : 0);
        result = 31 * result + (sortOrder != null ? sortOrder.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Query{" +
                "uri=" + uri +
                ", projection=" + projection +
                ", selection='" + selection + '\'' +
                ", selectionArgs=" + selectionArgs +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }

    public static class Builder {
        private Uri uri;
        private List<String> projection;
        private String selection;
        private List<String> selectionArgs;
        private String sortOrder;

        /**
         * The URI to query. This will be the full URI sent by the client; if the client is requesting a specific record, the URI will end in a record number that the implementation should parse and add to a WHERE or HAVING clause, specifying that _id value.
         *
         * @param uri uri
         * @return builder
         */
        @NonNull public Builder uri(@NonNull Uri uri) {
            this.uri = uri;
            return this;
        }

        /**
         * The list of columns to put into the cursor. If null all columns are included.
         *
         * @param columns columns
         * @return builder
         */
        @NonNull public Builder projection(@Nullable String... columns) {
            projection = columns == null || columns.length == 0
                    ? null
                    : new ArrayList<>(Arrays.asList(columns));
            return this;
        }

        /**
         * A selection criteria to apply when filtering rows. If null then all rows are included.
         *
         * @param selection selection
         * @return builder
         */
        @NonNull public Builder selection(@Nullable String selection) {
            this.selection = selection;
            return this;
        }

        /**
         * You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
         *
         * @param selectionArgs selection arguments
         * @return builder
         */
        @NonNull public Builder selectionArgs(@Nullable String... selectionArgs) {
            this.selectionArgs = selectionArgs == null || selectionArgs.length == 0
                    ? null
                    : new ArrayList<>(Arrays.asList(selectionArgs));
            return this;
        }

        /**
         * How the rows in the cursor should be sorted. If null then the provider is free to define the sort order.
         *
         * @param sortOrder sort order
         * @return builder
         */
        @NonNull public Builder sortOrder(@Nullable String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        /**
         * Builds query, can throw {@link IllegalStateException} if something is incorrect
         *
         * @return query
         */
        @NonNull public Query build() {
            if (uri == null) {
                throw new IllegalStateException("Please specify uri");
            }

            return new Query(
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            );
        }
    }
}
