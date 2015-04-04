package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.operation.PreparedOperation;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Prepared Delete Operation for {@link StorIOContentResolver}
 *
 * @param <T> type of result of Delete Operation
 */
public abstract class PreparedDelete<T> implements PreparedOperation<T> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    @NonNull
    protected final DeleteResolver deleteResolver;

    PreparedDelete(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteResolver deleteResolver) {
        this.storIOContentResolver = storIOContentResolver;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Builder for {@link PreparedDelete}
     */
    public static class Builder {

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
    }
}
