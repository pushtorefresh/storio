package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithObject<T> extends BasePreparedPutWithObject<PutResult, T> {

    @NonNull private final T object;

    PreparedPutWithObject(@NonNull BambooStorage bambooStorage, @NonNull T object,
                          @NonNull String table, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(bambooStorage, table, mapFunc);
        this.object = object;
    }

    @NonNull public PutResult executeAsBlocking() {
        return executeAutoPut(object);
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

        private String table;
        private MapFunc<T, ContentValues> mapFunc;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull T object) {
            this.bambooStorage = bambooStorage;
            this.object = object;
        }

        @NonNull public Builder<T> into(@NonNull String table) {
            this.table = table;
            return this;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public PreparedOperation<PutResult> prepare() {
            return new PreparedPutWithObject<>(
                    bambooStorage,
                    object,
                    table, mapFunc
            );
        }
    }
}
