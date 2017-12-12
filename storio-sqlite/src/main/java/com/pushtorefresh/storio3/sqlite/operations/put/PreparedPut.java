package com.pushtorefresh.storio3.sqlite.operations.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio3.operations.PreparedCompletableOperation;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;

import java.util.Arrays;
import java.util.Collection;

import static com.pushtorefresh.storio3.impl.ChainImpl.buildChain;

/**
 * Prepared Put Operation for {@link StorIOSQLite} which performs insert or update data
 * in {@link StorIOSQLite}.
 */
public abstract class PreparedPut<Result, Data> implements
    PreparedCompletableOperation<Result, Data> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    PreparedPut(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
    }

    /**
     * Executes Put Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null results of Put Operation.
     */
    @WorkerThread
    @NonNull
    public final Result executeAsBlocking() {
        return buildChain(storIOSQLite.interceptors(), getRealCallInterceptor())
                .proceed(this);
    }

    @NonNull
    protected abstract Interceptor getRealCallInterceptor();

    /**
     * Builder for {@link PreparedPut}.
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Prepares Put Operation for one instance of {@link ContentValues}.
         *
         * @param contentValues content values to put.
         * @return builder.
         */
        @NonNull
        public PreparedPutContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutContentValues.Builder(storIOSQLite, contentValues);
        }

        /**
         * Prepares Put Operation for multiple {@link ContentValues}.
         *
         * @param contentValuesIterable content values to put.
         * @return builder.
         */
        @NonNull
        public PreparedPutContentValuesIterable.Builder contentValues(@NonNull Iterable<ContentValues> contentValuesIterable) {
            return new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValuesIterable);
        }

        /**
         * Prepares Put Operation for multiple {@link ContentValues}.
         *
         * @param contentValuesArray content values to put.
         * @return builder.
         */
        @NonNull
        public PreparedPutContentValuesIterable.Builder contentValues(@NonNull ContentValues... contentValuesArray) {
            return new PreparedPutContentValuesIterable.Builder(storIOSQLite, Arrays.asList(contentValuesArray));
        }

        /**
         * Prepares Put Operation for one object.
         *
         * @param object object to put.
         * @param <T>    type of object.
         * @return builder.
         */
        @NonNull
        public <T> PreparedPutObject.Builder<T> object(T object) {
            return new PreparedPutObject.Builder<T>(storIOSQLite, object);
        }

        /**
         * Prepares Put Operation for multiple objects.
         *
         * @param objects objects to put.
         * @param <T>     type of objects.
         * @return builder.
         */
        @NonNull
        public <T> PreparedPutCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedPutCollectionOfObjects.Builder<T>(storIOSQLite, objects);
        }
    }
}
