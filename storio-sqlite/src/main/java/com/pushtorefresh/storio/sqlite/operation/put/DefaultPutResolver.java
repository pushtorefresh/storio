package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;
import com.pushtorefresh.storio.internal.QueryUtil;

/**
 * Default, thread-safe implementation of {@link PutResolver}
 *
 * @param <T> type of objects to put
 */
public abstract class DefaultPutResolver<T> extends PutResolver<T> {

    /**
     * Converts object of required type to {@link InsertQuery}
     *
     * @param object non-null object that should be converted to {@link InsertQuery}
     * @return non-null {@link InsertQuery}
     */
    @NonNull
    protected abstract InsertQuery mapToInsertQuery(@NonNull T object);

    /**
     * Converts object of required type to {@link UpdateQuery}
     *
     * @param object non-null object that should be converted to {@link UpdateQuery}
     * @return non-null {@link UpdateQuery}
     */
    @NonNull
    protected abstract UpdateQuery mapToUpdateQuery(@NonNull T object);

    /**
     * Converts object of required type to {@link ContentValues}
     *
     * @param object non-null object that should be converted to {@link ContentValues}
     * @return non-null {@link ContentValues}
     */
    @NonNull
    protected abstract ContentValues mapToContentValues(@NonNull T object);

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull T object) {
        final UpdateQuery updateQuery = mapToUpdateQuery(object);

        // for data consistency in concurrent environment, encapsulate Put Operation into transaction
        storIOSQLite.internal().beginTransaction();

        try {
            final Cursor cursor = storIOSQLite.internal().query(new Query.Builder()
                    .table(updateQuery.table)
                    .where(updateQuery.where)
                    .whereArgs((Object[]) QueryUtil.listToArray(updateQuery.whereArgs))
                    .build());

            final PutResult putResult;

            try {
                final ContentValues contentValues = mapToContentValues(object);

                if (cursor.getCount() == 0) {
                    final InsertQuery insertQuery = mapToInsertQuery(object);
                    final long insertedId = storIOSQLite.internal().insert(insertQuery, contentValues);
                    putResult = PutResult.newInsertResult(insertedId, insertQuery.table);
                } else {
                    final int numberOfRowsUpdated = storIOSQLite.internal().update(updateQuery, contentValues);
                    putResult = PutResult.newUpdateResult(numberOfRowsUpdated, updateQuery.table);
                }
            } finally {
                cursor.close();
            }

            // everything okay
            storIOSQLite.internal().setTransactionSuccessful();

            return putResult;
        } finally {
            // in case of bad situations, db won't be affected
            storIOSQLite.internal().endTransaction();
        }
    }
}
