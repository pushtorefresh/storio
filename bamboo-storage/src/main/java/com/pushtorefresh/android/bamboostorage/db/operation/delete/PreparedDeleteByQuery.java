package com.pushtorefresh.android.bamboostorage.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorage;
import com.pushtorefresh.android.bamboostorage.db.query.DeleteQuery;

import java.util.Collections;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

    @NonNull private final DeleteQuery deleteQuery;

    protected PreparedDeleteByQuery(@NonNull BambooStorage bambooStorage, @NonNull DeleteQuery deleteQuery) {
        super(bambooStorage);
        this.deleteQuery = deleteQuery;
    }

    @NonNull @Override public DeleteResult executeAsBlocking() {
        final BambooStorage.Internal internal = bambooStorage.internal();

        final int countOfDeletedRows = internal.delete(deleteQuery);
        internal.notifyAboutChanges(Collections.singleton(deleteQuery.table));

        return DeleteResult.newDeleteResult(countOfDeletedRows, Collections.singleton(deleteQuery.table));
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

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final DeleteQuery deleteQuery;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull DeleteQuery deleteQuery) {
            this.bambooStorage = bambooStorage;
            this.deleteQuery = deleteQuery;
        }

        @NonNull public PreparedDeleteByQuery prepare() {
            return new PreparedDeleteByQuery(bambooStorage, deleteQuery);
        }
    }
}
