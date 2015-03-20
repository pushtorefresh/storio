package com.pushtorefresh.storio.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.query.DeleteQuery;

import java.util.Collections;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

    @NonNull private final DeleteQuery deleteQuery;

    PreparedDeleteByQuery(@NonNull StorIODb storIODb, @NonNull DeleteQuery deleteQuery, @NonNull DeleteResolver deleteResolver) {
        super(storIODb, deleteResolver);
        this.deleteQuery = deleteQuery;
    }

    @NonNull @Override public DeleteResult executeAsBlocking() {
        final StorIODb.Internal internal = storIODb.internal();

        final int numberOfDeletedRows = deleteResolver.performDelete(storIODb, deleteQuery);
        internal.notifyAboutChanges(new Changes(deleteQuery.table));

        return DeleteResult.newDeleteResult(numberOfDeletedRows, Collections.singleton(deleteQuery.table));
    }

    @NonNull @Override public Observable<DeleteResult> createObservable() {
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

        @NonNull private final StorIODb storIODb;
        @NonNull private final DeleteQuery deleteQuery;

        private DeleteResolver deleteResolver;

        Builder(@NonNull StorIODb storIODb, @NonNull DeleteQuery deleteQuery) {
            this.storIODb = storIODb;
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

            return new PreparedDeleteByQuery(storIODb, deleteQuery, deleteResolver);
        }
    }
}
