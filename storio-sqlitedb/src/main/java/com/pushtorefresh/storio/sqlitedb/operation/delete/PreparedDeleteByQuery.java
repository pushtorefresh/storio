package com.pushtorefresh.storio.sqlitedb.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlitedb.Changes;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.query.DeleteQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

    @NonNull private final DeleteQuery deleteQuery;

    PreparedDeleteByQuery(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull DeleteQuery deleteQuery, @NonNull DeleteResolver deleteResolver) {
        super(storIOSQLiteDb, deleteResolver);
        this.deleteQuery = deleteQuery;
    }

    @NonNull @Override public DeleteResult executeAsBlocking() {
        final StorIOSQLiteDb.Internal internal = storIOSQLiteDb.internal();

        final int numberOfDeletedRows = deleteResolver.performDelete(storIOSQLiteDb, deleteQuery);
        internal.notifyAboutChanges(Changes.newInstance(deleteQuery.table));

        return DeleteResult.newInstance(numberOfDeletedRows, deleteQuery.table);
    }

    @NonNull @Override public Observable<DeleteResult> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<DeleteResult>() {
            @Override public void call(Subscriber<? super DeleteResult> subscriber) {
                final DeleteResult deleteByQueryResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteByQueryResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder {

        @NonNull private final StorIOSQLiteDb storIOSQLiteDb;
        @NonNull private final DeleteQuery deleteQuery;

        private DeleteResolver deleteResolver;

        Builder(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull DeleteQuery deleteQuery) {
            this.storIOSQLiteDb = storIOSQLiteDb;
            this.deleteQuery = deleteQuery;
        }

        /**
         * Optional: Specifies {@link DeleteResolver} for Delete Operation
         *
         * @param deleteResolver delete resolver
         * @return builder
         */
        @NonNull public Builder withDeleteResolver(@NonNull DeleteResolver deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation
         * @return {@link PreparedDeleteByQuery} instance
         */
        @NonNull public PreparedDeleteByQuery prepare() {
            if (deleteResolver == null) {
                deleteResolver = DefaultDeleteResolver.INSTANCE;
            }

            return new PreparedDeleteByQuery(storIOSQLiteDb, deleteQuery, deleteResolver);
        }
    }
}
