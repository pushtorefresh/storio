package com.pushtorefresh.storio3.contentresolver.operations.delete;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio3.operations.PreparedCompletableOperation;

import java.util.Collection;

import static com.pushtorefresh.storio3.impl.ChainImpl.buildChain;
import static com.pushtorefresh.storio3.internal.Checks.checkNotNull;

/**
 * Prepared Delete Operation for {@link StorIOContentResolver}.
 *
 * @param <Result> type of result of Delete Operation.
 */
public abstract class PreparedDelete<Result, Data> implements
    PreparedCompletableOperation<Result, Data> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    PreparedDelete(@NonNull StorIOContentResolver storIOContentResolver) {
        this.storIOContentResolver = storIOContentResolver;
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
    @Override
    public Result executeAsBlocking() {
        return buildChain(storIOContentResolver.interceptors(), getRealCallInterceptor())
                .proceed(this);
    }

    @NonNull
    protected abstract Interceptor getRealCallInterceptor();

    /**
     * Builder for {@link PreparedDelete}.
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        /**
         * Creates new builder for {@link PreparedDelete}.
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}.
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Creates builder for {@link PreparedDeleteByQuery}.
         *
         * @param deleteQuery non-null delete query.
         * @return builder for {@link PreparedDeleteByQuery}.
         */
        @NonNull
        public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(storIOContentResolver, deleteQuery);
        }

        /**
         * Creates builder for {@link PreparedDeleteCollectionOfObjects}.
         *
         * @param objects non-null collection of objects to delete.
         * @param <T>     type of objects.
         * @return builder for {@link PreparedDeleteCollectionOfObjects}.
         */
        @NonNull
        public <T> PreparedDeleteCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedDeleteCollectionOfObjects.Builder<T>(storIOContentResolver, objects);
        }

        /**
         * Creates builder for {@link PreparedDeleteObject}.
         *
         * @param object non-null object to delete.
         * @param <T>    type of object.
         * @return builder for {@link PreparedDeleteObject}.
         */
        @NonNull
        public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<T>(storIOContentResolver, object);
        }
    }
}
