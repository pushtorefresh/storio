package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}
 *
 * @param <T> type of object to delete
 */
public class PreparedDeleteObject<T> extends PreparedDelete<DeleteResult> {

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
     * Executes Delete Operation immediately in current thread
     *
     * @return non-null result of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, object);
        storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(deleteResult.affectedTables()));
        return deleteResult;
    }

    /**
     * Creates an {@link Observable} which will emit result of Delete Operation
     *
     * @return non-null {@link Observable} which will emit non-null result of Delete Operation
     */
    @NonNull
    @Override
    public Observable<DeleteResult> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<DeleteResult>() {
            @Override
            public void call(Subscriber<? super DeleteResult> subscriber) {
                final DeleteResult deleteResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedDeleteObject}
     *
     * @param <T> type of object to delete
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
         * Optional: Specifies {@link DeleteResolver} for Delete Operation
         * <p/>
         * Can be set via {@link SQLiteTypeDefaults},
         * If resolver is not set via {@link SQLiteTypeDefaults} or explicitly -> exception will be thrown
         *
         * @param deleteResolver delete resolver
         * @return builder
         */
        @NonNull
        public Builder<T> withDeleteResolver(@NonNull DeleteResolver<T> deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation
         *
         * @return {@link PreparedDeleteObject} instance
         */
        @SuppressWarnings("unchecked")
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            final SQLiteTypeDefaults<T> typeDefinition = storIOSQLite.internal().typeDefaults((Class<T>) object.getClass());

            if (deleteResolver == null && typeDefinition != null) {
                deleteResolver = typeDefinition.deleteResolver;
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
