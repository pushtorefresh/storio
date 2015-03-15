package com.pushtorefresh.storio.contentprovider.query;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Insert query for {@link com.pushtorefresh.storio.contentprovider.StorIOContentProvider}
 * <p>
 * Instances of this class are Immutable
 */
public class InsertQuery {

    /**
     * The content:// URI of the insertion request.
     */
    @NonNull public final Uri uri;

    private InsertQuery(@NonNull Uri uri) {
        this.uri = uri;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InsertQuery that = (InsertQuery) o;

        return uri.equals(that.uri);

    }

    @Override public int hashCode() {
        return uri.hashCode();
    }

    @Override public String toString() {
        return "InsertQuery{" +
                "uri=" + uri +
                '}';
    }

    /**
     * Builder for {@link InsertQuery}
     * <p>
     * Yep, it looks stupid with only one parameter — Uri, but think about future,
     * we can add other things later without breaking the API!
     */
    public static class Builder {

        private Uri uri;

        /**
         * Specifies uri
         *
         * @param uri content:// URI of the insertion request
         */
        @NonNull public Builder uri(@NonNull Uri uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Builds {@link InsertQuery} instance with required params,
         * can throw {@link IllegalStateException} if something is incorrect
         *
         * @return new {@link Query} instance
         */
        @NonNull InsertQuery build() {
            if (uri == null) {
                throw new IllegalStateException("Please specify uri");
            }

            return new InsertQuery(
                    uri
            );
        }
    }
}
