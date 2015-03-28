package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.InsertQuery;
import com.pushtorefresh.storio.db.query.UpdateQuery;

/**
 * Default, thread-safe implementation of {@link PutResolver}
 *
 * @param <T> type of objects to put
 */
public abstract class DefaultPutResolver<T> implements PutResolver<T> {

    /**
     * Resolves table name to perform insert or update for it
     *
     * @return table name
     */
    @NonNull
    protected abstract String getTable();

    /**
     * Performs insert or update of {@link ContentValues} into {@link StorIODb}
     * <p>
     * By default, it will perform insert if content values does not contain {@link BaseColumns#_ID} field with non-null long
     * or update if content values contains {@link BaseColumns#_ID} field and value
     *
     * But, if it will decide to perform update and no rows will be updated, it will perform insert!
     *
     * @param storIODb      instance of {@link StorIODb}
     * @param contentValues content values to put
     * @return non-null result of put operation
     */
    @Override
    @NonNull
    public PutResult performPut(@NonNull StorIODb storIODb, @NonNull ContentValues contentValues) {
        final Long id = contentValues.getAsLong(BaseColumns._ID);
        final String table = getTable();

        return id == null
                ? insert(storIODb, contentValues)
                : updateOrInsert(storIODb, contentValues, table, id);
    }

    @NonNull
    private PutResult insert(@NonNull StorIODb storIODb, @NonNull ContentValues contentValues) {
        final String table = getTable();

        final long insertedId = storIODb.internal().insert(
                new InsertQuery.Builder()
                        .table(table)
                        .nullColumnHack(null)
                        .build(),
                contentValues
        );
        return PutResult.newInsertResult(insertedId, table);
    }

    @NonNull
    private PutResult updateOrInsert(@NonNull StorIODb storIODb, @NonNull ContentValues contentValues, @NonNull String table, @NonNull Long id) {
        final int numberOfUpdatedRows = storIODb.internal().update(
                new UpdateQuery.Builder()
                        .table(table)
                        .where(BaseColumns._ID + "=?")
                        .whereArgs(String.valueOf(id))
                        .build(),
                contentValues
        );

        return numberOfUpdatedRows > 0
                ? PutResult.newUpdateResult(numberOfUpdatedRows, table)
                : insert(storIODb, contentValues);
    }
}
