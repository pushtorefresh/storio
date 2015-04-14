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

    PreparedDeleteByQuery(@NonNull StorIOSQLite storIOSQLite, @NonNull DeleteQuery deleteQuery) {
        super(storIOSQLite);
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
        final int numberOfRowsDeleted = storIOSQLite.internal().delete(deleteQuery);
        storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(deleteQuery.table));
        return DeleteResult.newInstance(numberOfRowsDeleted, deleteQuery.table);
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

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull DeleteQuery deleteQuery) {
            this.storIOSQLite = storIOSQLite;
            this.deleteQuery = deleteQuery;
        }

        /**
         * Prepares Delete Operation
         *
         * @return {@link PreparedDeleteByQuery} instance
         */
        @NonNull
        public PreparedDeleteByQuery prepare() {
            return new PreparedDeleteByQuery(storIOSQLite, deleteQuery);
        }
    }
}
