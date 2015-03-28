package com.pushtorefresh.storio.sqlitedb.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;

/**
 * Implements Put Operation behavior (insert or update)
 *
 * @param <T> type of objects to put
 * @see {@link DefaultPutResolver} default implementation
 */
public interface PutResolver<T> {

    /**
     * Performs Put Operation of some {@link ContentValues} into {@link StorIOSQLiteDb}
     *
     * @param storIOSQLiteDb      instance of {@link StorIOSQLiteDb}
     * @param contentValues some {@link ContentValues} to put
     * @return non-null result of Put Operation
     */
    @NonNull
    PutResult performPut(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull ContentValues contentValues);

    /**
     * Useful callback which will be called in same thread that performed Put Operation right after
     * execution of {@link #performPut(StorIOSQLiteDb, ContentValues)}
     * <p>
     * You can, for example, set object id after insert
     *
     * @param object    object, that was "put" in {@link StorIOSQLiteDb}
     * @param putResult result of put operation
     */
    void afterPut(@NonNull T object, @NonNull PutResult putResult);
}
