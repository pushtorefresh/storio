package com.pushtorefresh.storio3.sqlite.operations.get;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.queries.GetQuery;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;

import static com.pushtorefresh.storio3.impl.ChainImpl.buildChain;

/**
 * Prepared Get Operation for {@link StorIOSQLite}.
 *
 * @param <Result> type of result.
 */
public abstract class PreparedGet<Result, WrappedResult> implements PreparedOperation<Result, WrappedResult, GetQuery> {

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
     * Executes Get Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return result of an operation. Can be null in get(Object).
     */
    @WorkerThread
    @Nullable
    public final Result executeAsBlocking() {
        return buildChain(storIOSQLite.interceptors(), getRealCallInterceptor())
                .proceed(this);
    }

    @NonNull
    protected abstract Interceptor getRealCallInterceptor();

    @NonNull
    @Override
    public GetQuery getData() {
        if (rawQuery != null) {
            return rawQuery;
        } else if (query != null) {
            return query;
        } else {
            throw new IllegalStateException("Either rawQuery or query should be set!");
        }
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
