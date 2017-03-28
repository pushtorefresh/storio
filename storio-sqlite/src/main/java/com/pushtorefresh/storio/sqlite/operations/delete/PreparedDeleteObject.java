package com.pushtorefresh.storio.sqlite.operations.delete;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.internal.RxJavaUtils;

import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of object to delete.
 */
public class PreparedDeleteObject<T> extends PreparedDelete<DeleteResult> {

    @NonNull
    private final T object;

    @Nullable
    private final DeleteResolver<T> explicitDeleteResolver;

    PreparedDeleteObject(@NonNull StorIOSQLite storIOSQLite,
                         @NonNull T object,
                         @Nullable DeleteResolver<T> explicitDeleteResolver) {
        super(storIOSQLite);
        this.object = object;
        this.explicitDeleteResolver = explicitDeleteResolver;
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
    @SuppressWarnings("unchecked")
    @WorkerThread
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        try {
            final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

            final DeleteResolver<T> deleteResolver;

            if (explicitDeleteResolver != null) {
                deleteResolver = explicitDeleteResolver;
            } else {
                final SQLiteTypeMapping<T> typeMapping
                        = lowLevel.typeMapping((Class<T>) object.getClass());

                if (typeMapping == null) {
                    throw new IllegalStateException("Object does not have type mapping: " +
                            "object = " + object + ", object.class = " + object.getClass() + ", " +
                            "db was not affected by this operation, please add type mapping for this type");
                }

                deleteResolver = typeMapping.deleteResolver();
            }

            final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, object);
            if (deleteResult.numberOfRowsDeleted() > 0) {
                final Changes changes = Changes.newInstance(
                        deleteResult.affectedTables(),
                        deleteResult.affectedTags()
                );
                lowLevel.notifyAboutChanges(changes);
            }
            return deleteResult;

        } catch (Exception exception) {
            throw new StorIOException("Error has occurred during Delete operation. object = " + object, exception);
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
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * and send result to observer.
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
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<DeleteResult> asRxObservable() {
        return RxJavaUtils.createObservable(storIOSQLite, this);
    }

    /**
     * Creates {@link Single} which will perform Delete Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Delete Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<DeleteResult> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
   }

    /**
     * Creates {@link Completable} which will perform Delete Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Delete Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOSQLite, this);
    }

    /**
     * Builder for {@link PreparedDeleteObject}.
     *
     * @param <T> type of object to delete.
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final T object;

        private DeleteResolver<T> deleteResolver;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull T object) {
            this.storIOSQLite = storIOSQLite;
            this.object = object;
        }

        /**
         * Optional: Specifies {@link DeleteResolver} for Delete Operation.
         * <p>
         * Can be set via {@link SQLiteTypeMapping},
         * If resolver is not set via {@link SQLiteTypeMapping}
         * or explicitly — exception will be thrown.
         *
         * @param deleteResolver delete resolver.
         * @return builder.
         */
        @NonNull
        public Builder<T> withDeleteResolver(@NonNull DeleteResolver<T> deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation.
         *
         * @return {@link PreparedDeleteObject} instance.
         */
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            return new PreparedDeleteObject<T>(
                    storIOSQLite,
                    object,
                    deleteResolver
            );
        }
    }
}
