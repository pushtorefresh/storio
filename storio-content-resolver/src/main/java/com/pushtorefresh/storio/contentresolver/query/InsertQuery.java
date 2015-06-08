package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;
import android.support.annotation.NonNull;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Insert query for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 * <p/>
 * Instances of this class are Immutable.
 */
public final class InsertQuery {

    @NonNull
    private final Uri uri;

    /**
     * Please use {@link com.pushtorefresh.storio.contentresolver.query.InsertQuery.Builder}
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
     * Builder for {@link InsertQuery}.
     * <p/>
     * Yep, it looks stupid with only one parameter — Uri, but think about future,
     * we can add other things later without breaking the API!
     */
    public static final class Builder {

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
            checkNotNull(uri, "Uri should not be null");
            return new CompleteBuilder(Uri.parse(uri));
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private final Uri uri;

        CompleteBuilder(@NonNull Uri uri) {
            this.uri = uri;
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
