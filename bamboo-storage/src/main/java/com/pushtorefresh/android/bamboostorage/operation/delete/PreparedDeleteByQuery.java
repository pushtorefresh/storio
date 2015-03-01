package com.pushtorefresh.android.bamboostorage.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteByQuery extends PreparedDelete<DeleteByQueryResult> {

    @NonNull private final DeleteQuery deleteQuery;

    protected PreparedDeleteByQuery(@NonNull BambooStorage bambooStorage, @NonNull DeleteQuery deleteQuery) {
        super(bambooStorage);
        this.deleteQuery = deleteQuery;
    }

    @NonNull @Override public DeleteByQueryResult executeAsBlocking() {
        int countOfDeletedRows = bambooStorage.internal().delete(deleteQuery);
        return new DeleteByQueryResult(deleteQuery, countOfDeletedRows);
    }

    @NonNull @Override public Observable<DeleteByQueryResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteByQueryResult>() {
            @Override public void call(Subscriber<? super DeleteByQueryResult> subscriber) {
                final DeleteByQueryResult deleteByQueryResult = executeAsBlocking();

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
