package com.pushtorefresh.storio.contentprovider.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Update query for {@link com.pushtorefresh.storio.contentprovider.StorIOContentProvider}
 * <p>
 * Instances of this class are Immutable
 */
public class UpdateQuery {

    /**
     * The URI to query. This can potentially have a record ID if this is an update request for a specific record
     */
    @NonNull public final Uri uri;

    /**
     * An optional filter to match rows to update.
     */
    @Nullable public final String where;

    /**
     * Arguments for {@link #where}
     */
    @Nullable public final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.contentprovider.query.UpdateQuery.Builder} instead of constructor
     */
    protected UpdateQuery(@NonNull Uri uri, @Nullable String where, @Nullable List<String> whereArgs) {
        this.uri = uri;
        this.where = where;
        this.whereArgs = whereArgs != null && !whereArgs.isEmpty()
                ? Collections.unmodifiableList(whereArgs)
                : null;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateQuery that = (UpdateQuery) o;

        if (!uri.equals(that.uri)) return false;
        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        return !(whereArgs != null ? !whereArgs.equals(that.whereArgs) : that.whereArgs != null);

    }

    @Override public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? whereArgs.hashCode() : 0);
        return result;
    }

    /**
     * Builder for {@link UpdateQuery}
     */
    public static class Builder {

        private Uri uri;
        private String where;
        private List<String> whereArgs;

        /**
         * Specifies uri
         *
         * @param uri URI to query. This can potentially have a record ID if this is an update request for a specific record
         * @return builder
         */
        @NonNull public Builder uri(@NonNull Uri uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Specifies where clause
         *
         * @param where an optional filter to match rows to update.
         * @return builder
         */
        @NonNull public Builder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Specifies arguments for where clause
         *
         * @param whereArgs arguments for {@link UpdateQuery#where}
         * @return builder
         */
        @NonNull public Builder whereArgs(@Nullable String... whereArgs) {
            this.whereArgs = whereArgs != null && whereArgs.length != 0
                    ? Arrays.asList(whereArgs)
                    : null;

            return this;
        }

        /**
         * Builds new instance of {@link UpdateQuery},
         * can throw {@link IllegalStateException} if something is incorrect
         *
         * @return new instance of {@link UpdateQuery}
         */
        @NonNull public UpdateQuery build() {
            if (uri == null) {
                throw new IllegalStateException("Please specify uri");
            }

            return new UpdateQuery(
                    uri,
                    where,
                    whereArgs
            );
        }
    }
}
