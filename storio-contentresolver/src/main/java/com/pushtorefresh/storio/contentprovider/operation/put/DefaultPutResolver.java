package com.pushtorefresh.storio.contentprovider.operation.put;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;
import com.pushtorefresh.storio.contentprovider.query.InsertQuery;
import com.pushtorefresh.storio.contentprovider.query.UpdateQuery;

/**
 * Default thread-safe implementation of {@link PutResolver}
 *
 * @param <T> type of objects to put
 */
public abstract class DefaultPutResolver<T> implements PutResolver<T> {

    /**
     * Resolves Uri to perform insert or update
     *
     * @param contentValues some {@link ContentValues} which will be "put" into {@link StorIOContentResolver}
     * @return non-null Uri for insert or update
     */
    @NonNull
    protected abstract Uri getUri(@NonNull ContentValues contentValues);

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
     * Performs Put Operation of some {@link ContentValues} into {@link StorIOContentResolver}
     * <p/>
     * By default, it will perform insert if content values does not contain {@link BaseColumns#_ID} field with non-null value
     * or update if content values contains {@link BaseColumns#_ID} field and value is not null
     * <p/>
     * But, if it will decide to perform update and no rows will be updated, it will perform insert!
     *
     * @param storIOContentProvider instance of {@link StorIOContentResolver}
     * @param contentValues         some {@link ContentValues} to put
     * @return non-null result of Put Operation
     */
    @Override
    public PutResult performPut(@NonNull StorIOContentResolver storIOContentProvider, @NonNull ContentValues contentValues) {
        final Uri uri = getUri(contentValues);
        final String idColumnName = getIdColumnName();

        final Object idAsObject = contentValues.get(idColumnName);
        final String idAsString = idAsObject != null
                ? idAsObject.toString()
                : null;

        return idAsString == null
                ? insert(storIOContentProvider, contentValues, uri)
                : updateOrInsert(storIOContentProvider, contentValues, uri, idColumnName, idAsString);
    }

    @NonNull
    private PutResult insert(@NonNull StorIOContentResolver storIOContentProvider, @NonNull ContentValues contentValues, @NonNull Uri uri) {
        final Uri insertedUri = storIOContentProvider
                .internal()
                .insert(new InsertQuery.Builder()
                                .uri(uri)
                                .build(),
                        contentValues
                );

        return PutResult.newInsertResult(insertedUri, uri);
    }

    @NonNull
    private PutResult updateOrInsert(@NonNull StorIOContentResolver storIOContentProvider,
                                     @NonNull ContentValues contentValues, @NonNull Uri uri,
                                     @NonNull String idColumnName, @NonNull String id) {
        final int numberOfRowsUpdated = storIOContentProvider
                .internal()
                .update(new UpdateQuery.Builder()
                                .uri(uri)
                                .where(idColumnName + "=?")
                                .whereArgs(id)
                                .build(),
                        contentValues
                );

        return numberOfRowsUpdated > 0
                ? PutResult.newUpdateResult(numberOfRowsUpdated, uri)
                : insert(storIOContentProvider, contentValues, uri);
    }

    /**
     * Useful callback which will be called in same thread that performed Put Operation right after
     * execution of {@link #performPut(StorIOContentResolver, ContentValues)}
     * <p/>
     * You can, for example, set object Uri after insert
     *
     * @param object,   that was "put" in {@link StorIOContentResolver}
     * @param putResult result of put operation
     */
    @Override
    public void afterPut(@NonNull T object, @NonNull PutResult putResult) {

    }
}
