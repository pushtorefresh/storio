package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;

import rx.Completable;
import rx.Observable;
import rx.Single;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Prepared Delete Operation for
 * {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 */
public class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

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
     * @return non-null result of Delete Operation.
     */
    @WorkerThread
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        try {
            return deleteResolver.performDelete(storIOContentResolver, deleteQuery);
        } catch (Exception exception) {
            throw new StorIOException("Error has occurred during Delete operation. query = " + deleteQuery, exception);
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
     * <dd>Operates on {@link StorIOContentResolver#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * And send result to observer.
     * @deprecated (will be removed in 2.0) please use {@link #asRxObservable()}.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<DeleteResult> createObservable() {
        return asRxObservable();
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<DeleteResult> asRxObservable() {
        return RxJavaUtils.createObservable(storIOContentResolver, this);
    }

    /**
     * Creates {@link Single} which will perform Delete Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Delete Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<DeleteResult> asRxSingle() {
        return RxJavaUtils.createSingle(storIOContentResolver, this);
    }

    /**
     * Creates {@link Completable} which will perform Delete Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Delete Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOContentResolver, this);
    }

    /**
     * Builder for {@link PreparedDeleteByQuery}.
     */
    public static class Builder {

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
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}.
         * @param deleteQuery           non-null instance of {@link DeleteQuery}.
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
