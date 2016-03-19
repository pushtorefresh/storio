package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.operations.PreparedWriteOperation;

import java.util.Collection;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Prepared Delete Operation for {@link StorIOContentResolver}.
 *
 * @param <Result> type of result of Delete Operation.
 */
public abstract class PreparedDelete<Result> implements PreparedWriteOperation<Result> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    PreparedDelete(@NonNull StorIOContentResolver storIOContentResolver) {
        this.storIOContentResolver = storIOContentResolver;
    }

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
