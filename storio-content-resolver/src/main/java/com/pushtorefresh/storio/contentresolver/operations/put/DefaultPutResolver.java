package com.pushtorefresh.storio.contentresolver.operations.put;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import static com.pushtorefresh.storio.internal.InternalQueries.nullableArrayOfStringsFromListOfStrings;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableString;

/**
 * Default thread-safe implementation of {@link PutResolver}.
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
    public PutResult performPut(@NonNull StorIOContentResolver storIOContentResolver, @NonNull T object) {
        final UpdateQuery updateQuery = mapToUpdateQuery(object);

        final Query query = Query.builder()
                .uri(updateQuery.uri())
                .where(nullableString(updateQuery.where()))
                .whereArgs((Object[]) nullableArrayOfStringsFromListOfStrings(updateQuery.whereArgs()))
                .build();

        final StorIOContentResolver.LowLevel lowLevel = storIOContentResolver.lowLevel();

        final Cursor cursor = lowLevel.query(query);

        try {
            final ContentValues contentValues = mapToContentValues(object);

            if (cursor.getCount() == 0) {
                final InsertQuery insertQuery = mapToInsertQuery(object);
                final Uri insertedUri = lowLevel.insert(insertQuery, contentValues);
                return PutResult.newInsertResult(insertedUri, insertQuery.uri());
            } else {
                final int numberOfRowsUpdated = lowLevel.update(updateQuery, contentValues);
                return PutResult.newUpdateResult(numberOfRowsUpdated, updateQuery.uri());
            }
        } finally {
            cursor.close();
        }
    }
}
