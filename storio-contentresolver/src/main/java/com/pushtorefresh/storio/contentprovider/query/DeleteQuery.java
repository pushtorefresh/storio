package com.pushtorefresh.storio.contentprovider.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.util.QueryUtil;

import java.util.List;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Delete query for {@link com.pushtorefresh.storio.contentprovider.StorIOContentResolver}
 * <p>
 * Instances of this class are Immutable
 */
public class DeleteQuery {

    /**
     * The full URI to query, including a row ID (if a specific record is requested)
     */
    @NonNull
    public final Uri uri;

    /**
     * An optional restriction to apply to rows when deleting
     */
    @Nullable
    public final String where;

    /**
     * Arguments for {@link #where}
     */
    @Nullable
    public final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.contentprovider.query.DeleteQuery.Builder} instead of constructor
     */
    protected DeleteQuery(@NonNull Uri uri, @Nullable String where, @Nullable List<String> whereArgs) {
        this.uri = uri;
        this.where = where;
        this.whereArgs = QueryUtil.listToUnmodifiable(whereArgs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteQuery that = (DeleteQuery) o;

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

    @Override
    public String toString() {
        return "DeleteQuery{" +
                "uri=" + uri +
                ", where='" + where + '\'' +
                ", whereArgs=" + whereArgs +
                '}';
    }

    /**
     * Builder for {@link DeleteQuery}
     */
    public static class Builder {

        private Uri uri;
        private String where;
        private List<String> whereArgs;

        /**
         * Specifies uri
         *
         * @param uri full URI to query, including a row ID (if a specific record is requested)
         * @return builder
         */
        @NonNull
        public Builder uri(@NonNull Uri uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Specifies where clause
         *
         * @param where optional restriction to apply to rows when deleting
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
         * Passed objects will be immediately converted to list of {@link String} via calling {@link Object#toString()}
         * <p>
         * Default value is <code>null</code>
         *
         * @param whereArgs arguments for {@link DeleteQuery#where}
         * @return builder
         */
        @NonNull
        public Builder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = QueryUtil.varargsToList(whereArgs);
            return this;
        }

        /**
         * Builds new instance of {@link DeleteQuery},
         * can throw {@link IllegalStateException} if something is incorrect
         *
         * @return new instance of {@link DeleteQuery}
         */
        @NonNull
        public DeleteQuery build() {
            checkNotNull(uri, "Please specify uri");

            return new DeleteQuery(
                    uri,
                    where,
                    whereArgs
            );
        }
    }
}
