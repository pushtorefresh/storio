package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}.
 */
public final class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

    @NonNull
    private final DeleteQuery deleteQuery;

    @NonNull
    private final DeleteResolver<DeleteQuery> deleteResolver;

    PreparedDeleteByQuery(@NonNull StorIOSQLite storIOSQLite, @NonNull DeleteQuery deleteQuery, @NonNull DeleteResolver<DeleteQuery> deleteResolver) {
        super(storIOSQLite);
        this.deleteQuery = deleteQuery;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Executes Delete Operation immediately in current thread.
     *
     * @return non-null result of Delete Operation.
     */
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, deleteQuery);
        storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(deleteResult.affectedTables()));
        return deleteResult;
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer.
     * <p/>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p/>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * and send result to observer.
     */
    @NonNull
    @Override
    public Observable<DeleteResult> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedDeleteByQuery}.
     */
    public static final class Builder {

        private static final DeleteResolver<DeleteQuery> STANDARD_DELETE_RESOLVER = new DefaultDeleteResolver<DeleteQuery>() {
            @NonNull
            @Override
            public DeleteQuery mapToDeleteQuery(@NonNull DeleteQuery deleteQuery) {
                return deleteQuery; // no transformations
            }
        };

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final DeleteQuery deleteQuery;

        private DeleteResolver<DeleteQuery> deleteResolver;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull DeleteQuery deleteQuery) {
            this.storIOSQLite = storIOSQLite;
            this.deleteQuery = deleteQuery;
        }

        /**
         * Optional: Specifies Delete Resolver for Delete Operation.
         * <p/>
         * If no value was specified, builder will use resolver that
         * simply redirects query to {@link StorIOSQLite}.
         *
         * @param deleteResolver nullable resolver for Delete Operation.
         * @return builder.
         */
        @NonNull
        public Builder withDeleteResolver(@Nullable DeleteResolver<DeleteQuery> deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation.
         *
         * @return {@link PreparedDeleteByQuery} instance.
         */
        @NonNull
        public PreparedDeleteByQuery prepare() {
            if (deleteResolver == null) {
                deleteResolver = STANDARD_DELETE_RESOLVER;
            }

            return new PreparedDeleteByQuery(storIOSQLite, deleteQuery, deleteResolver);
        }
    }
}
