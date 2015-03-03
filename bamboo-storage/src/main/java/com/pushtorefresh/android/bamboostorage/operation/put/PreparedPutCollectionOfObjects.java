package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutCollectionOfObjects<T>
        extends BasePreparedPutWithObject<PutCollectionOfObjectResult<T>, T> {

    @NonNull private final Collection<T> objects;

    PreparedPutCollectionOfObjects(
            @NonNull BambooStorage bambooStorage, @NonNull Collection<T> objects,
            @NonNull String table, @NonNull MapFunc<T, ContentValues> mapFunc) {

        super(bambooStorage, table, mapFunc);

        this.objects = objects;
    }

    @NonNull @Override public PutCollectionOfObjectResult<T> executeAsBlocking() {
        final Map<T, PutResult> results = new HashMap<>();

        for (T object : objects) {
            results.put(object, executeAutoPut(object));
        }

        return new PutCollectionOfObjectResult<>(results);
    }

    @NonNull @Override public Observable<PutCollectionOfObjectResult<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutCollectionOfObjectResult<T>>() {
            @Override
            public void call(Subscriber<? super PutCollectionOfObjectResult<T>> subscriber) {
                PutCollectionOfObjectResult<T> putCollectionOfObjectResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putCollectionOfObjectResult);
                    subscriber.onCompleted();
                }
            }
        });
    }


    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final Collection<T> objects;

        private String table;
        private MapFunc<T, ContentValues> mapFunc;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Collection<T> objects) {
            this.bambooStorage = bambooStorage;
            this.objects = objects;
        }

        @NonNull public Builder<T> into(@NonNull String table) {
            this.table = table;
            return this;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public PreparedPutCollectionOfObjects<T> prepare() {
            return new PreparedPutCollectionOfObjects<>(
                    bambooStorage,
                    objects,
                    table, mapFunc
            );
        }
    }
}
