package com.pushtorefresh.android.bamboostorage.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorageDb;
import com.pushtorefresh.android.bamboostorage.db.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.db.operation.PreparedOperation;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithObject<T> extends PreparedPut<T, PutResult> {

    @NonNull private final T object;
    @NonNull private final MapFunc<T, ContentValues> mapFunc;

    PreparedPutWithObject(@NonNull BambooStorageDb bambooStorageDb, @NonNull PutResolver<T> putResolver,
                          @NonNull T object, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(bambooStorageDb, putResolver);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @NonNull public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(
                bambooStorageDb,
                mapFunc.map(object)
        );

        putResolver.afterPut(object, putResult);
        bambooStorageDb.internal().notifyAboutChanges(putResult.affectedTables());

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

        @NonNull private final BambooStorageDb bambooStorageDb;
        @NonNull private final T object;

        private MapFunc<T, ContentValues> mapFunc;
        private PutResolver<T> putResolver;

        public Builder(@NonNull BambooStorageDb bambooStorageDb, @NonNull T object) {
            this.bambooStorageDb = bambooStorageDb;
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
                    bambooStorageDb,
                    putResolver,
                    object,
                    mapFunc
            );
        }
    }
}
