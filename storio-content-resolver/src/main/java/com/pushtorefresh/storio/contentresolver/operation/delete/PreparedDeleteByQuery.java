package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Delete Operation by {@link com.pushtorefresh.storio.contentresolver.query.DeleteQuery}
 * for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}
 */
public final class PreparedDeleteByQuery extends PreparedDelete<DeleteQuery, DeleteResult> {

    @NonNull
    private final DeleteQuery deleteQuery;

    PreparedDeleteByQuery(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteResolver<DeleteQuery> deleteResolver, @NonNull DeleteQuery deleteQuery) {
        super(storIOContentResolver, deleteResolver);
        this.deleteQuery = deleteQuery;
    }

    /**
     * Executes Delete Operation immediately in current thread
     *
     * @return non-null result of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        return deleteResolver.performDelete(storIOContentResolver, deleteQuery);
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Delete Operation and send result to observer
     */
    @NonNull
    @Override
    public Observable<DeleteResult> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Builder for {@link PreparedDeleteByQuery}
     */
    public static final class Builder {

        private static final DeleteResolver<DeleteQuery> STANDARD_DELETE_RESOLVER = new DefaultDeleteResolver<DeleteQuery>() {
            @NonNull
            @Override
            protected DeleteQuery mapToDeleteQuery(@NonNull DeleteQuery deleteQuery) {
                return deleteQuery; // easy
            }
        };

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final DeleteQuery deleteQuery;

        private DeleteResolver<DeleteQuery> deleteResolver;

        /**
         * Creates builder for {@link PreparedDeleteByQuery}
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}
         * @param deleteQuery           non-null instance of {@link DeleteQuery}
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteQuery deleteQuery) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(deleteQuery, "Please specify delete query");

            this.storIOContentResolver = storIOContentResolver;
            this.deleteQuery = deleteQuery;
        }

        /**
         * Optional: Specifies resolver for Delete Operation
         * Allows you to customise behavior of Delete Operation
         * <p/>
         * If no value will be set, builder will use Delete Resolver that simply redirects query to {@link StorIOContentResolver}
         *
         * @param deleteResolver nullable resolver for Delete Operation
         * @return builder
         */
        @NonNull
        public Builder withDeleteResolver(@Nullable DeleteResolver<DeleteQuery> deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Builds instance of {@link PreparedDeleteByQuery}
         *
         * @return instance of {@link PreparedDeleteByQuery}
         */
        @NonNull
        public PreparedDeleteByQuery prepare() {
            if (deleteResolver == null) {
                deleteResolver = STANDARD_DELETE_RESOLVER;
            }

            return new PreparedDeleteByQuery(
                    storIOContentResolver,
                    deleteResolver,
                    deleteQuery
            );
        }
    }
}
