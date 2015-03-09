package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithObject<T> extends PreparedPut<T, PutResult> {

    @NonNull private final T object;
    @NonNull private final MapFunc<T, ContentValues> mapFunc;

    PreparedPutWithObject(@NonNull BambooStorage bambooStorage, @NonNull PutResolver<T> putResolver,
                          @NonNull T object, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(bambooStorage, putResolver);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @NonNull public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(
                bambooStorage,
                mapFunc.map(object)
        );

        putResolver.afterPut(object, putResult);
        bambooStorage.internal().notifyAboutChanges(putResult.affectedTables());

        return putResult;
    }

    @NonNull public Observable<PutResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutResult>() {
            @Override
            public void call(Subscriber<? super PutResult> subscriber) {
                final PutResult putResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final T object;

        private MapFunc<T, ContentValues> mapFunc;
        private PutResolver<T> putResolver;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull T object) {
            this.bambooStorage = bambooStorage;
            this.object = object;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        @NonNull public PreparedOperation<PutResult> prepare() {
            return new PreparedPutWithObject<>(
                    bambooStorage,
                    putResolver,
                    object,
                    mapFunc
            );
        }
    }
}
