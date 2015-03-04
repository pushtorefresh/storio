package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutObjects<T> extends PreparedPut<T, PutCollectionResult<T>> {

    @NonNull private final Iterable<T> objects;
    @NonNull private final MapFunc<T, ContentValues> mapFunc;


    public PreparedPutObjects(@NonNull BambooStorage bambooStorage,
                              @NonNull PutResolver<T> putResolver,
                              @NonNull Iterable<T> objects, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(bambooStorage, putResolver);
        this.objects = objects;
        this.mapFunc = mapFunc;
    }

    @NonNull @Override public PutCollectionResult<T> executeAsBlocking() {
        final Map<T, PutResult> putResultsMap = new HashMap<>();

        for (T object : objects) {
            final PutResult putResult = putResolver.performPut(bambooStorage, mapFunc.map(object));
            putResolver.afterPut(object, putResult);
            putResultsMap.put(object, putResult);
        }

        return new PutCollectionResult<>(putResultsMap);
    }

    @NonNull @Override public Observable<PutCollectionResult<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutCollectionResult<T>>() {
            @Override
            public void call(Subscriber<? super PutCollectionResult<T>> subscriber) {
                PutCollectionResult<T> putResults = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResults);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final Iterable<T> objects;

        private MapFunc<T, ContentValues> mapFunc;
        private PutResolver<T> putResolver;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Iterable<T> objects) {
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

        @NonNull public PreparedPutObjects<T> prepare() {
            return new PreparedPutObjects<>(
                    bambooStorage,
                    putResolver,
                    objects,
                    mapFunc
            );
        }
    }
}
