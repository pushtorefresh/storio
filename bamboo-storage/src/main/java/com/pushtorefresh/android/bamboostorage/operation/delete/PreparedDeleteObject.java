package com.pushtorefresh.android.bamboostorage.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteObject<T> extends PreparedDelete<DeleteObjectResult<T>> {

    @NonNull private final T object;
    @NonNull private final MapFunc<T, DeleteQuery> mapFunc;

    protected PreparedDeleteObject(@NonNull BambooStorage bambooStorage, @NonNull T object, @NonNull MapFunc<T, DeleteQuery> mapFunc) {
        super(bambooStorage);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @NonNull @Override public DeleteObjectResult<T> executeAsBlocking() {
        final DeleteQuery deleteQuery = mapFunc.map(object);
        final int countOfDeletedRows = bambooStorage.internal().delete(deleteQuery);
        return new DeleteObjectResult<>(object, deleteQuery, countOfDeletedRows);
    }

    @NonNull @Override public Observable<DeleteObjectResult<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteObjectResult<T>>() {
            @Override public void call(Subscriber<? super DeleteObjectResult<T>> subscriber) {
                final DeleteObjectResult<T> deleteObjectResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteObjectResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final T object;

        private MapFunc<T, DeleteQuery> mapFunc;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull T object) {
            this.bambooStorage = bambooStorage;
            this.object = object;
        }

        @NonNull public Builder<T> mapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public PreparedDeleteObject<T> prepare() {
            return new PreparedDeleteObject<>(bambooStorage, object, mapFunc);
        }
    }
}
