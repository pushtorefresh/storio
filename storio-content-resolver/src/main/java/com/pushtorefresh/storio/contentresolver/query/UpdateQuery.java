package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.Queries;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Update query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 * <p/>
 * Instances of this class are Immutable.
 */
public final class UpdateQuery {

    @NonNull
    private final Uri uri;

    @Nullable
    private final String where;

    @Nullable
    private final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.query.UpdateQuery.Builder}
     * instead of constructor.
     */
    private UpdateQuery(@NonNull Uri uri, @Nullable String where, @Nullable List<String> whereArgs) {
        this.uri = uri;
        this.where = where;
        this.whereArgs = Queries.listToUnmodifiable(whereArgs);
    }

    /**
     * Gets {@code content://} URI of the insertion request.
     * <p/>
     * This can potentially have a record ID if this is an update request for a specific record.
     *
     * @return non-null URI of the update request.
     */
    @NonNull
    public Uri uri() {
        return uri;
    }

    /**
     * Gets {@code WHERE} clause.
     * <p/>
     * Optional filter declaring which rows to update.
     * <p/>
     * Formatted as an SQL {@code WHERE} clause (excluding the {@code WHERE} itself).
     * <p/>
     * If it's {@code null} — Query will update all rows for specified URI.
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
     * Builder for {@link UpdateQuery}.
     */
    public static final class Builder {

        /**
         * Required: Specifies uri.
         *
         * @param uri URI to query.
         *            This can potentially have a record ID if this is an update
         *            request for a specific record.
         * @return builder.
         * @see UpdateQuery#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull Uri uri) {
            checkNotNull(uri, "Please specify uri");
            return new CompleteBuilder(uri);
        }

        /**
         * Required: Specifies uri.
         *
         * @param uri URI to query.
         *            This can potentially have a record ID if this is an update
         *            request for a specific record.
         * @return builder.
         * @see UpdateQuery#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull String uri) {
            return new CompleteBuilder(Uri.parse(uri));
        }
    }

    /**
     * Compile-time safe part of {@link Builder}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private final Uri uri;

        private String where;

        private List<String> whereArgs;

        CompleteBuilder(@NonNull Uri uri) {
            this.uri = uri;
        }

        /**
         * Optional: Specifies where clause.
         * <p/>
         * Default value is {@code null}.
         *
         * @param where an optional filter to match rows to update.
         * @return builder.
         * @see UpdateQuery#where()
         */
        @NonNull
        public CompleteBuilder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for where clause.
         * <p/>
         * Passed objects will be immediately converted to
         * list {@link String} via calling {@link Object#toString()}.
         * <p/>
         * Default value is {@code null}.
         *
         * @param whereArgs arguments for {@link UpdateQuery#where}.
         * @return builder.
         * @see UpdateQuery#whereArgs()
         */
        @NonNull
        public CompleteBuilder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = Queries.varargsToList(whereArgs);
            return this;
        }

        /**
         * Builds new instance of {@link UpdateQuery}.
         *
         * @return new instance of {@link UpdateQuery}.
         */
        @NonNull
        public UpdateQuery build() {
            return new UpdateQuery(
                    uri,
                    where,
                    whereArgs
            );
        }
    }
}
