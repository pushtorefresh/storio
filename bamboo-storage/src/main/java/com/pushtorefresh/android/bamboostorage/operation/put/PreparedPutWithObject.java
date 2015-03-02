package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.query.InsertQueryBuilder;
import com.pushtorefresh.android.bamboostorage.query.UpdateQueryBuilder;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithObject<T> extends PreparedPut<PutResult> {

    @NonNull private final T object;
    @NonNull private final String table;
    @NonNull private final MapFunc<T, ContentValues> mapFunc;

    PreparedPutWithObject(@NonNull BambooStorage bambooStorage, @NonNull T object,
                          @NonNull String table, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(bambooStorage);
        this.object = object;
        this.table = table;
        this.mapFunc = mapFunc;
    }

    @NonNull public PutResult executeAsBlocking() {
        final ContentValues contentValues = mapFunc.map(object);

        final Long id = contentValues.getAsLong(BaseColumns._ID);

        if (id == null) {
            final long insertedId = bambooStorage.internal().insert(
                    new InsertQueryBuilder()
                            .table(table)
                            .nullColumnHack(null)
                            .build(),
                    contentValues
            );

            return new PutResult(insertedId, null);
        } else {
            int numberOfUpdatedRows = bambooStorage.internal().update(
                    new UpdateQueryBuilder()
                            .table(table)
                            .where(BaseColumns._ID + "=?")
                            .whereArgs(String.valueOf(id))
                            .build(),
                    contentValues
            );

            return new PutResult(null, numberOfUpdatedRows);
        }
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
