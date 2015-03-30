package com.pushtorefresh.storio.sqlitedb.operation.put;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.query.InsertQuery;
import com.pushtorefresh.storio.sqlitedb.query.UpdateQuery;

/**
 * Default, thread-safe implementation of {@link PutResolver}
 *
 * @param <T> type of objects to put
 */
public abstract class DefaultPutResolver<T> implements PutResolver<T> {

    /**
     * Resolves table name to perform insert or update
     *
     * @return table name
     */
    @NonNull
    protected abstract String getTable();

    /**
     * Performs insert or update of {@link ContentValues} into {@link StorIOSQLiteDb}
     * <p>
     * By default, it will perform insert if content values does not contain {@link BaseColumns#_ID} field with non-null value
     * or update if content values contains {@link BaseColumns#_ID} field and value is not null
     * <p>
     * But, if it will decide to perform update and no rows will be updated, it will perform insert!
     *
     * @param storIOSQLiteDb instance of {@link StorIOSQLiteDb}
     * @param contentValues  content values to put
     * @return non-null result of put operation
     */
    @Override
    @NonNull
    public PutResult performPut(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull ContentValues contentValues) {
        final Long id = contentValues.getAsLong(getIdFieldName());
        final String table = getTable();

        return id == null
                ? insert(storIOSQLiteDb, contentValues, table)
                : updateOrInsert(storIOSQLiteDb, contentValues, table, id);
    }

    @NonNull
    private PutResult insert(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull ContentValues contentValues, @NonNull String table) {
        final long insertedId = storIOSQLiteDb.internal().insert(
                new InsertQuery.Builder()
                        .table(table)
                        .nullColumnHack(null)
                        .build(),
                contentValues
        );

        return PutResult.newInsertResult(insertedId, table);
    }

    @NonNull
    private PutResult updateOrInsert(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull ContentValues contentValues, @NonNull String table, @NonNull Long id) {
        final int numberOfUpdatedRows = storIOSQLiteDb.internal().update(
                new UpdateQuery.Builder()
                        .table(table)
                        .where(getIdFieldName() + "=?")
                        .whereArgs(String.valueOf(id))
                        .build(),
                contentValues
        );

        return numberOfUpdatedRows > 0
                ? PutResult.newUpdateResult(numberOfUpdatedRows, table)
                : insert(storIOSQLiteDb, contentValues, table);
    }

    @NonNull
    protected String getIdFieldName() {
        return BaseColumns._ID;
    }
}
