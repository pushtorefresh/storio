package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.InsertQueryBuilder;
import com.pushtorefresh.storio.db.query.UpdateQueryBuilder;

import java.util.Collections;

public abstract class DefaultPutResolver<T> implements PutResolver<T> {

    @NonNull protected abstract String getTable();

    @Override
    @NonNull
    public PutResult performPut(@NonNull StorIODb storIODb, @NonNull ContentValues contentValues) {
        final Long id = contentValues.getAsLong(BaseColumns._ID);
        final String table = getTable();

        if (id == null) {
            final long insertedId = storIODb.internal().insert(
                    new InsertQueryBuilder()
                            .table(table)
                            .nullColumnHack(null)
                            .build(),
                    contentValues
            );

            return PutResult.newInsertResult(insertedId, Collections.singleton(table));
        } else {
            final int numberOfUpdatedRows = storIODb.internal().update(
                    new UpdateQueryBuilder()
                            .table(table)
                            .where(BaseColumns._ID + "=?")
                            .whereArgs(String.valueOf(id))
                            .build(),
                    contentValues
            );

            return PutResult.newUpdateResult(numberOfUpdatedRows, Collections.singleton(table));
        }
    }
}
