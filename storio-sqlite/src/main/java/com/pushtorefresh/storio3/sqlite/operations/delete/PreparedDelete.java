package com.pushtorefresh.storio3.sqlite.operations.delete;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio3.operations.PreparedCompletableOperation;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery;

import java.util.Collection;

import static com.pushtorefresh.storio3.impl.ChainImpl.buildChain;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of object to delete.
 */
public abstract class PreparedDelete<T, Data> implements PreparedCompletableOperation<T, Data> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    PreparedDelete(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
    }

    /**
     * Executes Delete Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null result of Delete Operation.
     */
    @WorkerThread
    @NonNull
    public final T executeAsBlocking() {
        return buildChain(storIOSQLite.interceptors(), getRealCallInterceptor())
                .proceed(this);
    }

    @NonNull
    protected abstract Interceptor getRealCallInterceptor();

    /**
     * Builder for {@link PreparedDelete}.
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Prepares Delete Operation by {@link com.pushtorefresh.storio3.sqlite.queries.DeleteQuery}.
         *
         * @param deleteQuery query that specifies which rows should be deleted.
         * @return builder.
         */
        @NonNull
        public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery);
        }

        /**
         * Prepares Delete Operation which should delete one object.
         *
         * @param object object to delete.
         * @param <T>    type of the object.
         * @return builder.
         */
        @NonNull
        public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<T>(storIOSQLite, object);
        }

        /**
         * Prepares Delete Operation which should delete multiple objects.
         *
         * @param objects objects to delete.
         * @param <T>     type of objects.
         * @return builder.
         */
        @NonNull
        public <T> PreparedDeleteCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedDeleteCollectionOfObjects.Builder<T>(storIOSQLite, objects);
        }
    }
}
