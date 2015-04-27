package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.operation.PreparedOperation;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Prepared Delete Operation for {@link StorIOContentResolver}
 *
 * @param <Result> type of result of Delete Operation
 */
public abstract class PreparedDelete<T, Result> implements PreparedOperation<Result> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    @NonNull
    protected final DeleteResolver<T> deleteResolver;

    PreparedDelete(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteResolver<T> deleteResolver) {
        this.storIOContentResolver = storIOContentResolver;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Builder for {@link PreparedDelete}
     */
    public static final class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        /**
         * Creates new builder for {@link PreparedDelete}
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Creates builder for {@link PreparedDeleteByQuery}
         *
         * @param deleteQuery non-null delete query
         * @return builder for {@link PreparedDeleteByQuery}
         */
        @NonNull
        public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(storIOContentResolver, deleteQuery);
        }

        /**
         * Creates builder for {@link PreparedDeleteObjects}
         *
         * @param type    type of objects, due to limitations of Generics in Java we have to explicitly ask you about type of objects, sorry :(
         * @param objects non-null collection of objects to delete
         * @param <T>     type of objects
         * @return builder for {@link PreparedDeleteObjects}
         */
        @NonNull
        public <T> PreparedDeleteObjects.Builder<T> objects(@NonNull Class<T> type, @NonNull Iterable<T> objects) {
            return new PreparedDeleteObjects.Builder<T>(storIOContentResolver, type, objects);
        }

        /**
         * Creates builder for {@link PreparedDeleteObject}
         *
         * @param object non-null object to delete
         * @param <T>    type of object
         * @return builder for {@link PreparedDeleteObject}
         */
        @NonNull
        public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<T>(storIOContentResolver, object);
        }
    }
}
