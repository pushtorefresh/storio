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

public class PreparedPutCollectionOfObjects<T> extends PreparedPut<T, PutCollectionOfObjectsResult<T>> {

    @NonNull private final Collection<T> objects;
    @NonNull private final MapFunc<T, ContentValues> mapFunc;


    public PreparedPutCollectionOfObjects(@NonNull BambooStorage bambooStorage,
                                          @NonNull PutResolver<T> putResolver,
                                          @NonNull Collection<T> objects, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(bambooStorage, putResolver);
        this.objects = objects;
        this.mapFunc = mapFunc;
    }

    @NonNull @Override public PutCollectionOfObjectsResult<T> executeAsBlocking() {
        final Map<T, PutResult> putResultsMap = new HashMap<>();

        for (T object : objects) {
            final PutResult putResult = putResolver.performPut(bambooStorage, mapFunc.map(object));
            putResolver.afterPut(object, putResult);
            putResultsMap.put(object, putResult);
        }

        return new PutCollectionOfObjectsResult<>(putResultsMap);
    }

    @NonNull @Override public Observable<PutCollectionOfObjectsResult<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutCollectionOfObjectsResult<T>>() {
            @Override
            public void call(Subscriber<? super PutCollectionOfObjectsResult<T>> subscriber) {
                PutCollectionOfObjectsResult<T> putResults = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResults);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final Collection<T> objects;

        private MapFunc<T, ContentValues> mapFunc;
        private PutResolver<T> putResolver;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Collection<T> objects) {
            this.bambooStorage = bambooStorage;
            this.objects = objects;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        @NonNull public PreparedPutCollectionOfObjects<T> prepare() {
            return new PreparedPutCollectionOfObjects<>(
                    bambooStorage,
                    putResolver,
                    objects,
                    mapFunc
            );
        }
    }
}
