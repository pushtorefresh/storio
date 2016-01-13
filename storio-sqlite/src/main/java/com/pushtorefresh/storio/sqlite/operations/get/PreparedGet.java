package com.pushtorefresh.storio.sqlite.operations.get;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

/**
 * Prepared Get Operation for {@link StorIOSQLite}.
 *
 * @param <Result> type of result.
 */
public abstract class PreparedGet<Result> implements PreparedOperation<Result> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    @Nullable
    protected final Query query;

    @Nullable
    protected final RawQuery rawQuery;

    PreparedGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        this.storIOSQLite = storIOSQLite;
        this.query = query;
        this.rawQuery = null;
    }

    PreparedGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        this.storIOSQLite = storIOSQLite;
        this.rawQuery = rawQuery;
        query = null;
    }

    /**
     * Builder for {@link PreparedGet}.
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Returns builder for Get Operation that returns result as {@link android.database.Cursor}.
         *
         * @return builder for Get Operation that returns result as {@link android.database.Cursor}.
         */
        @NonNull
        public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(storIOSQLite);
        }

        /**
         * Returns builder for Get Operation that returns result as {@link java.util.List} of items.
         *
         * @param type type of items.
         * @param <T>  type of items.
         * @return builder for Get Operation that returns result as {@link java.util.List} of items.
         */
        @NonNull
        public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<T>(storIOSQLite, type);
        }

        /**
         * Returns builder for Get Operation that returns result as item instance.
         *
         * @param type type of item.
         * @param <T>  type of item.
         * @return builder for Get Operation that returns result as item instance.
         */
        @NonNull
        public <T> PreparedGetObject.Builder<T> object(@NonNull Class<T> type) {
            return new PreparedGetObject.Builder<T>(storIOSQLite, type);
        }

        /**
         * Returns builder for Get Operation that returns number of results.
         *
         * @return builder for Get Operation that returns number of results.
         */
        @NonNull
        public PreparedGetNumberOfResults.Builder numberOfResults() {
            return new PreparedGetNumberOfResults.Builder(storIOSQLite);
        }
    }
}
