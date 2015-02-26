package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.query.InsertQueryBuilder;
import com.pushtorefresh.android.bamboostorage.query.UpdateQuery;
import com.pushtorefresh.android.bamboostorage.query.UpdateQueryBuilder;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithObject<T> extends PreparedPut<SinglePutResult> {

    @NonNull private final T object;
    @NonNull private final MapFunc<T, ContentValues> mapFunc;
    @NonNull private final PutResolver<T> putResolver;
    @Nullable private final UpdateQuery updateQuery;

    PreparedPutWithObject(@NonNull BambooStorage bambooStorage, @NonNull T object,
                          @NonNull MapFunc<T, ContentValues> mapFunc, @NonNull PutResolver<T> putResolver,
                          @Nullable UpdateQuery updateQuery) {
        super(bambooStorage);
        this.object = object;
        this.mapFunc = mapFunc;
        this.putResolver = putResolver;
        this.updateQuery = updateQuery;
    }

    @NonNull public SinglePutResult executeAsBlocking() {
        if (updateQuery != null) {
            return executeUpdateQuery();
        } else {
            return executeAutoPut();
        }
    }

    @NonNull private SinglePutResult executeUpdateQuery() {
        //noinspection ConstantConditions
        int updatedRowsCount = bambooStorage.getInternal().update(updateQuery, mapFunc.map(object));
        return new SinglePutResult(null, updatedRowsCount);
    }

    @NonNull private SinglePutResult executeAutoPut() {
        Long internalId = putResolver.getInternalIdValue(object);

        if (internalId == null) {
            long insertedRowId = bambooStorage.getInternal()
                    .insert(new InsertQueryBuilder()
                                    .table(putResolver.getTableName(object))
                                    .build(),
                            mapFunc.map(object)
                    );

            putResolver.setInternalId(object, insertedRowId);

            return new SinglePutResult(insertedRowId, null);
        } else {
            int updatedRowsCount = bambooStorage.getInternal()
                    .update(new UpdateQueryBuilder()
                                    .table(putResolver.getTableName(object))
                                    .where(putResolver.getInternalIdColumnName(object) + " = ?")
                                    .whereArgs(String.valueOf(internalId))
                                    .build(),
                            mapFunc.map(object)
                    );

            return new SinglePutResult(null, updatedRowsCount);
        }
    }


    @NonNull public Observable<SinglePutResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<SinglePutResult>() {
            @Override
            public void call(Subscriber<? super SinglePutResult> subscriber) {
                final SinglePutResult putResult = executeAsBlocking();

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
        private UpdateQuery updateQuery;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull T object) {
            this.bambooStorage = bambooStorage;
            this.object = object;
        }

        @NonNull public Builder<T> mapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> putResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        @NonNull public Builder<T> updateQuery(@NonNull UpdateQuery updateQuery) {
            this.updateQuery = updateQuery;
            return this;
        }

        @NonNull public PreparedOperation<SinglePutResult> prepare() {
            return new PreparedPutWithObject<>(
                    bambooStorage,
                    object,
                    mapFunc,
                    putResolver,
                    updateQuery
            );
        }
    }
}
