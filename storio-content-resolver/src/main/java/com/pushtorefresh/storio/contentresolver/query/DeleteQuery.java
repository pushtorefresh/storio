package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.Queries;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Delete query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 * <p>
 * Instances of this class are Immutable.
 */
public final class DeleteQuery {

    @NonNull
    private final Uri uri;

    @Nullable
    private final String where;

    @Nullable
    private final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.query.DeleteQuery.Builder}
     * instead of constructor.
     */
    private DeleteQuery(@NonNull Uri uri, @Nullable String where, @Nullable List<String> whereArgs) {
        this.uri = uri;
        this.where = where;
        this.whereArgs = Queries.unmodifiableNullableList(whereArgs);
    }

    /**
     * Gets full {@code URI} to query, including a row ID (if a specific record is requested).
     *
     * @return non-null URI to query.
     */
    @NonNull
    public Uri uri() {
        return uri;
    }

    /**
     * Gets {@code WHERE} clause.
     * <p>
     * An optional restriction to apply to rows when deleting.
     * <p>
     * If {@code null} — all rows will be deleted.
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
     * Builder for {@link DeleteQuery}.
     */
    public static final class Builder {

        /**
         * Required: Specifies uri.
         *
         * @param uri full {@code URI} to query, including a row ID
         *            (if a specific record is requested).
         * @return builder.
         * @see DeleteQuery#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull Uri uri) {
            checkNotNull(uri, "Please specify uri");
            return new CompleteBuilder(uri);
        }

        /**
         * Required: Specifies uri.
         *
         * @param uri full {@code URI} to query,
         *            including a row ID (if a specific record is requested).
         * @return builder.
         * @see DeleteQuery#uri()
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

        private String where;
        private List<String> whereArgs;

        CompleteBuilder(@NonNull Uri uri) {
            this.uri = uri;
        }

        /**
         * Optional: Specifies {@code WHERE} clause.
         * <p>
         * Default value is {@code null}.
         *
         * @param where optional restriction to apply to rows when deleting.
         * @return builder.
         * @see DeleteQuery#where()
         */
        @NonNull
        public CompleteBuilder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for where clause.
         * <p>
         * Passed objects will be immediately converted to list
         * of {@link String} via calling {@link Object#toString()}.
         * <p>
         * Default value is {@code null}.
         *
         * @param whereArgs arguments for {@link DeleteQuery#where}.
         * @return builder.
         * @see DeleteQuery#whereArgs()
         */
        @NonNull
        public CompleteBuilder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = Queries.varargsToList(whereArgs);
            return this;
        }

        /**
         * Builds new instance of {@link DeleteQuery}.
         *
         * @return new instance of {@link DeleteQuery}.
         */
        @NonNull
        public DeleteQuery build() {
            return new DeleteQuery(
                    uri,
                    where,
                    whereArgs
            );
        }
    }
}
