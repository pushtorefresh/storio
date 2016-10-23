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
 * Delete query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 * <p>
 * Instances of this class are Immutable.
 */
public final class DeleteQuery {

    @NonNull
    private final Uri uri;

    @NonNull
    private final String where;

    @NonNull
    private final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.queries.DeleteQuery.Builder}
     * instead of constructor.
     */
    private DeleteQuery(@NonNull Uri uri, @Nullable String where, @Nullable List<String> whereArgs) {
        this.uri = uri;
        this.where = nonNullString(where);
        this.whereArgs = unmodifiableNonNullList(whereArgs);
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
     * If empty — all rows will be deleted.
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

        DeleteQuery that = (DeleteQuery) o;

        if (!uri.equals(that.uri)) return false;
        if (!where.equals(that.where)) return false;
        return whereArgs.equals(that.whereArgs);
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + where.hashCode();
        result = 31 * result + whereArgs.hashCode();
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
     * Creates new builder for {@link DeleteQuery}.
     *
     * @return non-null instance of {@link DeleteQuery.Builder}.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link DeleteQuery}.
     */
    public static final class Builder {

        /**
         * Please use {@link DeleteQuery#builder()} instead of this.
         */
        Builder() {
        }

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

        private String where;
        private List<String> whereArgs;

        CompleteBuilder(@NonNull Uri uri) {
            this.uri = uri;
        }

        CompleteBuilder(@NonNull DeleteQuery deleteQuery) {
            this.uri = deleteQuery.uri;
            this.where = deleteQuery.where;
            this.whereArgs = deleteQuery.whereArgs;
        }

        /**
         * Specifies uri.
         *
         * @param uri full {@code URI} to query, including a row ID
         *            (if a specific record is requested).
         * @return builder.
         * @see DeleteQuery#uri()
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
         * @see DeleteQuery#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull String uri) {
            checkNotEmpty(uri, "Uri should not be null");
            this.uri = Uri.parse(uri);
            return this;
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
        public <T> CompleteBuilder whereArgs(@Nullable T... whereArgs) {
            this.whereArgs = unmodifiableNonNullListOfStrings(whereArgs);
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
