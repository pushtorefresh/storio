package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;
import rx.Subscriber;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}
 */
public class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

    @NonNull
    private final DeleteQuery deleteQuery;

    PreparedDeleteByQuery(@NonNull StorIOSQLite storIOSQLite, @NonNull DeleteQuery deleteQuery, @NonNull DeleteResolver deleteResolver) {
        super(storIOSQLite, deleteResolver);
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
        final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, deleteQuery);
        storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(deleteQuery.table));
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
                final DeleteResult deleteByQueryResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteByQueryResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedDeleteByQuery}
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;
        @NonNull
        private final DeleteQuery deleteQuery;

        private DeleteResolver deleteResolver;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull DeleteQuery deleteQuery) {
            this.storIOSQLite = storIOSQLite;
            this.deleteQuery = deleteQuery;
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
        public Builder withDeleteResolver(@NonNull DeleteResolver deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation
         *
         * @return {@link PreparedDeleteByQuery} instance
         */
        @NonNull
        public PreparedDeleteByQuery prepare() {
            if (deleteResolver == null) {
                deleteResolver = DefaultDeleteResolver.INSTANCE;
            }

            return new PreparedDeleteByQuery(storIOSQLite, deleteQuery, deleteResolver);
        }
    }
}
