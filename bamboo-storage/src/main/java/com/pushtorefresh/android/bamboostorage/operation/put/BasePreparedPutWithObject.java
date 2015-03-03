package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.query.InsertQueryBuilder;
import com.pushtorefresh.android.bamboostorage.query.UpdateQueryBuilder;

abstract class BasePreparedPutWithObject<Result, T> extends PreparedPut<Result> {

    @NonNull final String table;
    @NonNull final MapFunc<T, ContentValues> mapFunc;

    BasePreparedPutWithObject(@NonNull BambooStorage bambooStorage,
                              @NonNull String table,
                              @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(bambooStorage);
        this.table = table;
        this.mapFunc = mapFunc;
    }

    @NonNull PutResult executeAutoPut(@NonNull final T object) {
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
}
