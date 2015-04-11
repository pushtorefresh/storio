package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;

/**
 * Implements Put Operation behavior (insert or update)
 *
 * @param <T> type of objects to put
 * @see {@link DefaultPutResolver} default implementation
 */
public interface PutResolver<T> {

    /**
     * Performs Put Operation of some {@link ContentValues} into {@link StorIOSQLite}
     *
     * @param storIOSQLite  instance of {@link StorIOSQLite}
     * @param contentValues some {@link ContentValues} to put
     * @return non-null result of Put Operation
     */
    @NonNull
    PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull ContentValues contentValues);

    /**
     * Useful callback which will be called in same thread that performed Put Operation right after
     * execution of {@link #performPut(StorIOSQLite, ContentValues)}
     * <p>
     * You can, for example, set object id after insert
     *
     * @param object    object, that was "put" in {@link StorIOSQLite}
     * @param putResult result of put operation
     */
    void afterPut(@NonNull T object, @NonNull PutResult putResult);
}
