package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Delete Operation for
 * {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 */
public final class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

    @NonNull
    private final DeleteQuery deleteQuery;

    @NonNull
    private final DeleteResolver<DeleteQuery> deleteResolver;

    PreparedDeleteByQuery(@NonNull StorIOContentResolver storIOContentResolver,
                          @NonNull DeleteQuery deleteQuery,
                          @NonNull DeleteResolver<DeleteQuery> deleteResolver) {
        super(storIOContentResolver);
        this.deleteQuery = deleteQuery;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Executes Delete Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return not-null result of Delete Operation.
     */
    @WorkerThread
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        try {
            return deleteResolver.performDelete(storIOContentResolver, deleteQuery);
        } catch (Exception exception) {
            throw new StorIOException(exception);
        }
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return not-null {@link Observable} which will perform Delete Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
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
         * Creates builder for {@link PreparedDeleteByQuery}.
         *
         * @param storIOContentResolver not-null instance of {@link StorIOContentResolver}.
         * @param deleteQuery           not-null instance of {@link DeleteQuery}.
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteQuery deleteQuery) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(deleteQuery, "Please specify delete query");

            this.storIOContentResolver = storIOContentResolver;
            this.deleteQuery = deleteQuery;
        }

        /**
         * Optional: Specifies resolver for Delete Operation.
         * Allows you to customise behavior of Delete Operation.
         * <p>
         * If no value will be set, builder will use Delete Resolver
         * that simply redirects query to {@link StorIOContentResolver}.
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
         * Builds instance of {@link PreparedDeleteByQuery}.
         *
         * @return instance of {@link PreparedDeleteByQuery}.
         */
        @NonNull
        public PreparedDeleteByQuery prepare() {
            if (deleteResolver == null) {
                deleteResolver = STANDARD_DELETE_RESOLVER;
            }

            return new PreparedDeleteByQuery(
                    storIOContentResolver,
                    deleteQuery,
                    deleteResolver
            );
        }
    }
}
