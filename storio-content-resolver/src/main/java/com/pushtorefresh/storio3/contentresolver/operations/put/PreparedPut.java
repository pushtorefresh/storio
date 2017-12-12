package com.pushtorefresh.storio3.contentresolver.operations.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.operations.PreparedCompletableOperation;

import java.util.Collection;

import static com.pushtorefresh.storio3.impl.ChainImpl.buildChain;

/**
 * Represents an Operation for {@link StorIOContentResolver} which performs insert or update data
 * in {@link android.content.ContentProvider}.
 *
 */
public abstract class PreparedPut<Result, Data> implements
    PreparedCompletableOperation<Result, Data> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    protected PreparedPut(@NonNull StorIOContentResolver storIOContentResolver) {
        this.storIOContentResolver = storIOContentResolver;
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
        return buildChain(storIOContentResolver.interceptors(), getRealCallInterceptor())
                .proceed(this);
    }

    @NonNull
    protected abstract Interceptor getRealCallInterceptor();

    /**
     * Builder for {@link PreparedPut}.
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Prepares Put Operation that should put one object.
         *
         * @param object object to put.
         * @param <T>    type of object.
         * @return builder for {@link PreparedPutObject}.
         */
        @NonNull
        public <T> PreparedPutObject.Builder<T> object(@NonNull T object) {
            return new PreparedPutObject.Builder<T>(storIOContentResolver, object);
        }

        /**
         * Prepares Put Operation that should put multiple objects.
         *
         * @param objects objects to put.
         * @param <T>     type of objects.
         * @return builder for {@link PreparedPutCollectionOfObjects}.
         */
        @NonNull
        public <T> PreparedPutCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedPutCollectionOfObjects.Builder<T>(storIOContentResolver, objects);
        }

        /**
         * Prepares Put Operation that should put one instance of {@link ContentValues}.
         *
         * @param contentValues non-null content values to put.
         * @return builder for {@link PreparedPutContentValues}.
         */
        @NonNull
        public PreparedPutContentValues.Builder contentValues(@NonNull ContentValues contentValues) {
            return new PreparedPutContentValues.Builder(storIOContentResolver, contentValues);
        }

        /**
         * Prepares Put Operation that should put several instances of {@link ContentValues}.
         *
         * @param contentValues non-null collection of {@link ContentValues}.
         * @return builder for {@link PreparedPutContentValuesIterable}.
         */
        @NonNull
        public PreparedPutContentValuesIterable.Builder contentValues(@NonNull Iterable<ContentValues> contentValues) {
            return new PreparedPutContentValuesIterable.Builder(storIOContentResolver, contentValues);
        }
    }
}
