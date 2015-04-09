package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
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
    private final MapFunc<T, DeleteQuery> mapFunc;

    PreparedDeleteObject(@NonNull StorIOSQLite storIOSQLite, @NonNull T object, @NonNull MapFunc<T, DeleteQuery> mapFunc, @NonNull DeleteResolver deleteResolver) {
        super(storIOSQLite, deleteResolver);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    /**
     * Executes Delete Operation immediately in current thread
     *
     * @return non-null result of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        final StorIOSQLite.Internal internal = storIOSQLite.internal();
        final DeleteQuery deleteQuery = mapFunc.map(object);

        final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, deleteQuery);

        internal.getLoggi().v(deleteResult.numberOfRowsDeleted() + " object(s) deleted");

        internal.notifyAboutChanges(Changes.newInstance(deleteQuery.table));

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

        private MapFunc<T, DeleteQuery> mapFunc;
        private DeleteResolver deleteResolver;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull T object) {
            this.storIOSQLite = storIOSQLite;
            this.object = object;
        }

        /**
         * Specifies map function to map object to {@link DeleteQuery}
         *
         * @param mapFunc map function to map object to {@link DeleteQuery}
         * @return builder
         */
        @NonNull
        public Builder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }


        /**
         * Optional: Specifies {@link DeleteResolver} for Delete Operation
         * <p>
         * Default value is instance of {@link DefaultDeleteResolver}
         *
         * @param deleteResolver delete resolver
         * @return builder
         */
        @NonNull
        public Builder<T> withDeleteResolver(@NonNull DeleteResolver deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation
         *
         * @return {@link PreparedDeleteObject} instance
         */
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            if (deleteResolver == null) {
                deleteResolver = DefaultDeleteResolver.INSTANCE;
            }

            checkNotNull(mapFunc, "Please specify map function");

            return new PreparedDeleteObject<T>(
                    storIOSQLite,
                    object,
                    mapFunc,
                    deleteResolver
            );
        }
    }
}
