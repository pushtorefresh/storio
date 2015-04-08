package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;

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
     * Provides field name that uses for store internal identifier.
     * You can override this to use your custom name.
     * <p/>
     * Default value is <code>BaseColumns._ID</code>
     *
     * @return column name to store internal id.
     */
    @NonNull
    protected String getIdColumnName() {
        return BaseColumns._ID;
    }

    /**
     * Performs insert or update of {@link ContentValues} into {@link StorIOSQLite}
     * <p/>
     * By default, it will perform insert if content values does not contain {@link BaseColumns#_ID} field with non-null value
     * or update if content values contains {@link BaseColumns#_ID} field and value is not null
     * <p/>
     * But, if it will decide to perform update and no rows will be updated, it will perform insert!
     *
     * @param storIOSQLiteDb instance of {@link StorIOSQLite}
     * @param contentValues  content values to put
     * @return non-null result of put operation
     */
    @Override
    @NonNull
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLiteDb, @NonNull ContentValues contentValues) {
        final String idColumnName = getIdColumnName();

        final Object idAsObject = contentValues.get(idColumnName);
        final String idAsString = idAsObject != null
                ? idAsObject.toString()
                : null;

        final String table = getTable();

        return idAsString == null
                ? insert(storIOSQLiteDb, contentValues, table)
                : updateOrInsert(storIOSQLiteDb, contentValues, table, idColumnName, idAsString);
    }

    @NonNull
    private PutResult insert(@NonNull StorIOSQLite storIOSQLiteDb, @NonNull ContentValues contentValues, @NonNull String table) {
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
    private PutResult updateOrInsert(@NonNull StorIOSQLite storIOSQLiteDb,
                                     @NonNull ContentValues contentValues,
                                     @NonNull String table,
                                     @NonNull String idFieldName,
                                     @NonNull String id) {

        final int numberOfRowsUpdated = storIOSQLiteDb.internal().update(
                new UpdateQuery.Builder()
                        .table(table)
                        .where(idFieldName + "=?")
                        .whereArgs(id)
                        .build(),
                contentValues
        );

        return numberOfRowsUpdated > 0
                ? PutResult.newUpdateResult(numberOfRowsUpdated, table)
                : insert(storIOSQLiteDb, contentValues, table);
    }

    /**
     * Useful callback which will be called in same thread that performed Put Operation right after
     * execution of {@link #performPut(StorIOSQLite, ContentValues)}
     * <p>
     * You can, for example, set object id after insert
     *
     * @param object    object, that was "put" in {@link StorIOSQLite}
     * @param putResult result of put operation
     */
    @Override
    public void afterPut(@NonNull T object, @NonNull PutResult putResult) {

    }
}
