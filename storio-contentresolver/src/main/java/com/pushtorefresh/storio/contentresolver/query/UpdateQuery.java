package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.util.QueryUtil;

import java.util.List;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Update query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}
 * <p/>
 * Instances of this class are Immutable
 */
public class UpdateQuery {

    /**
     * The URI to query. This can potentially have a record ID if this is an update request for a specific record
     */
    @NonNull
    public final Uri uri;

    /**
     * An optional filter to match rows to update.
     */
    @Nullable
    public final String where;

    /**
     * Arguments for {@link #where}
     */
    @Nullable
    public final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.query.UpdateQuery.Builder} instead of constructor
     */
    protected UpdateQuery(@NonNull Uri uri, @Nullable String where, @Nullable List<String> whereArgs) {
        this.uri = uri;
        this.where = where;
        this.whereArgs = QueryUtil.listToUnmodifiable(whereArgs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateQuery that = (UpdateQuery) o;

        if (!uri.equals(that.uri)) return false;
        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        return !(whereArgs != null ? !whereArgs.equals(that.whereArgs) : that.whereArgs != null);

    }

    @Override
    public int hashCode() {
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
         * Required: Specifies uri
         *
         * @param uri URI to query. This can potentially have a record ID if this is an update request for a specific record
         * @return builder
         */
        @NonNull
        public Builder uri(@NonNull Uri uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Optional: Specifies where clause
         *
         * @param where an optional filter to match rows to update.
         * @return builder
         */
        @NonNull
        public Builder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for where clause
         * <p>
         * Passed objects will be immediately converted to list {@link String} via calling {@link Object#toString()}
         * <p>
         * Default value is <code>null</code>
         *
         * @param whereArgs arguments for {@link UpdateQuery#where}
         * @return builder
         */
        @NonNull
        public Builder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = QueryUtil.varargsToList(whereArgs);
            return this;
        }

        /**
         * Builds new instance of {@link UpdateQuery}
         *
         * @return new instance of {@link UpdateQuery}
         */
        @NonNull
        public UpdateQuery build() {
            checkNotNull(uri, "Please specify uri");

            return new UpdateQuery(
                    uri,
                    where,
                    whereArgs
            );
        }
    }
}
