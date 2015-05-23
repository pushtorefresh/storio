package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of object to delete.
 */
public final class PreparedDeleteObject<T> extends PreparedDelete<DeleteResult> {

    @NonNull
    private final T object;

    @NonNull
    private final DeleteResolver<T> deleteResolver;

    PreparedDeleteObject(@NonNull StorIOSQLite storIOSQLite, @NonNull T object, @NonNull DeleteResolver<T> deleteResolver) {
        super(storIOSQLite);
        this.object = object;
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
        final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, object);
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
     * Builder for {@link PreparedDeleteObject}.
     *
     * @param <T> type of object to delete.
     */
    public static final class Builder<T> {

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
         * <p/>
         * Can be set via {@link SQLiteTypeMapping},
         * If resolver is not set via {@link SQLiteTypeMapping}
         * or explicitly -> exception will be thrown.
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
        @SuppressWarnings("unchecked")
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            final SQLiteTypeMapping<T> typeDefinition = storIOSQLite.internal().typeMapping((Class<T>) object.getClass());

            if (deleteResolver == null && typeDefinition != null) {
                deleteResolver = typeDefinition.deleteResolver();
            }

            checkNotNull(deleteResolver, "Please specify DeleteResolver");

            return new PreparedDeleteObject<T>(
                    storIOSQLite,
                    object,
                    deleteResolver
            );
        }
    }
}
