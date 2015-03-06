package com.pushtorefresh.android.bamboostorage.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteCollectionOfObjects<T> extends PreparedDelete<DeleteCollectionOfObjectsResult<T>> {

    @NonNull private final Collection<T> objects;
    @NonNull private final MapFunc<T, DeleteQuery> mapFunc;

    protected PreparedDeleteCollectionOfObjects(@NonNull BambooStorage bambooStorage, @NonNull Collection<T> objects, @NonNull MapFunc<T, DeleteQuery> mapFunc) {
        super(bambooStorage);
        this.objects = objects;
        this.mapFunc = mapFunc;
    }

    @NonNull @Override public DeleteCollectionOfObjectsResult<T> executeAsBlocking() {
        BambooStorage.Internal bambooStorageInternal = bambooStorage.internal();

        Map<T, DeleteResult> results = new HashMap<>();

        for (T object : objects) {
            final DeleteQuery deleteQuery = mapFunc.map(object);
            final int countOfDeletedRows = bambooStorageInternal.delete(deleteQuery);

            results.put(object, new DeleteResult(deleteQuery, countOfDeletedRows));
        }

        return new DeleteCollectionOfObjectsResult<>(results);
    }

    @NonNull @Override public Observable<DeleteCollectionOfObjectsResult<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteCollectionOfObjectsResult<T>>() {
            @Override
            public void call(Subscriber<? super DeleteCollectionOfObjectsResult<T>> subscriber) {
                DeleteCollectionOfObjectsResult<T> deleteCollectionOfObjectsResult
                        = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteCollectionOfObjectsResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final Collection<T> objects;

        private MapFunc<T, DeleteQuery> mapFunc;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Collection<T> objects) {
            this.bambooStorage = bambooStorage;
            this.objects = objects;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public PreparedDeleteCollectionOfObjects<T> prepare() {
            return new PreparedDeleteCollectionOfObjects<>(bambooStorage, objects, mapFunc);
        }
    }
}
