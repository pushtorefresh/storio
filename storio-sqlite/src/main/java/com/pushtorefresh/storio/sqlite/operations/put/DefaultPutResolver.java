package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import static com.pushtorefresh.storio.internal.InternalQueries.nullableArrayOfStringsFromListOfStrings;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableString;

/**
 * Default implementation of {@link PutResolver}.
 * <p>
 * Thread-safe.
 *
 * @param <T> type of objects to put.
 */
public abstract class DefaultPutResolver<T> extends PutResolver<T> {

    /**
     * Converts object of required type to {@link InsertQuery}.
     *
     * @param object non-null object that should be converted to {@link InsertQuery}.
     * @return non-null {@link InsertQuery}.
     */
    @NonNull
    protected abstract InsertQuery mapToInsertQuery(@NonNull T object);

    /**
     * Converts object of required type to {@link UpdateQuery}.
     *
     * @param object non-null object that should be converted to {@link UpdateQuery}.
     * @return non-null {@link UpdateQuery}.
     */
    @NonNull
    protected abstract UpdateQuery mapToUpdateQuery(@NonNull T object);

    /**
     * Converts object of required type to {@link ContentValues}.
     *
     * @param object non-null object that should be converted to {@link ContentValues}.
     * @return non-null {@link ContentValues}.
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
        final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

        // for data consistency in concurrent environment, encapsulate Put Operation into transaction
        lowLevel.beginTransaction();

        try {
            final Cursor cursor = lowLevel.query(Query.builder()
                    .table(updateQuery.table())
                    .where(nullableString(updateQuery.where()))
                    .whereArgs((Object[]) nullableArrayOfStringsFromListOfStrings(updateQuery.whereArgs()))
                    .build());

            final PutResult putResult;

            try {
                final ContentValues contentValues = mapToContentValues(object);

                if (cursor.getCount() == 0) {
                    final InsertQuery insertQuery = mapToInsertQuery(object);
                    final long insertedId = lowLevel.insert(insertQuery, contentValues);
                    putResult = PutResult.newInsertResult(insertedId, insertQuery.table(), insertQuery.affectsTags());
                } else {
                    final int numberOfRowsUpdated = lowLevel.update(updateQuery, contentValues);
                    putResult = PutResult.newUpdateResult(numberOfRowsUpdated, updateQuery.table(), updateQuery.affectsTags());
                }
            } finally {
                cursor.close();
            }

            // everything okay
            lowLevel.setTransactionSuccessful();

            return putResult;
        } finally {
            // in case of bad situations, db won't be affected
            lowLevel.endTransaction();
        }
    }
}
