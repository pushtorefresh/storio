package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;
import android.support.annotation.NonNull;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Insert query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 * <p>
 * Instances of this class are Immutable.
 */
public final class InsertQuery {

    @NonNull
    private final Uri uri;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.queries.InsertQuery.Builder}
     * instead of constructor.
     */
    private InsertQuery(@NonNull Uri uri) {
        this.uri = uri;
    }

    /**
     * Gets {@code content://} URI of the insertion request.
     *
     * @return non-null URI of the insertion request.
     */
    @NonNull
    public Uri uri() {
        return uri;
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

        InsertQuery that = (InsertQuery) o;

        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public String toString() {
        return "InsertQuery{" +
                "uri=" + uri +
                '}';
    }

    /**
     * Creates new builder for {@link InsertQuery}.
     *
     * @return non-null instance of {@link InsertQuery.Builder}.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link InsertQuery}.
     * <p>
     * Yep, it looks stupid with only one parameter — Uri, but think about future,
     * we can add other things later without breaking the API!
     */
    public static final class Builder {

        /**
         * Please use {@link InsertQuery#builder()} instead of this.
         */
        Builder() {
        }

        /**
         * Required: Specifies uri.
         *
         * @param uri {@code content://} URI of the insertion request.
         * @return builder.
         * @see InsertQuery#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull Uri uri) {
            checkNotNull(uri, "Please specify uri");
            return new CompleteBuilder(uri);
        }

        /**
         * Required: Specifies uri.
         *
         * @param uri {@code content://} URI of the insertion request.
         * @return builder.
         * @see InsertQuery#uri()
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

        CompleteBuilder(@NonNull Uri uri) {
            this.uri = uri;
        }

        CompleteBuilder(@NonNull InsertQuery insertQuery) {
            this.uri = insertQuery.uri;
        }

        /**
         * Specifies uri.
         *
         * @param uri full {@code URI} to query, including a row ID
         *            (if a specific record is requested).
         * @return builder.
         * @see InsertQuery#uri()
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
         * @see InsertQuery#uri()
         */
        @NonNull
        public CompleteBuilder uri(@NonNull String uri) {
            checkNotEmpty(uri, "Uri should not be null");
            this.uri = Uri.parse(uri);
            return this;
        }

        /**
         * Builds {@link InsertQuery} instance with required params.
         *
         * @return new {@link InsertQuery} instance.
         */
        @NonNull
        public InsertQuery build() {
            return new InsertQuery(
                    uri
            );
        }
    }
}
