package com.pushtorefresh.storio.contentprovider.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;

/**
 * Implements Put Operation behavior (insert or update)
 *
 * @param <T> type of objects to put
 * @see {@link DefaultPutResolver} default implementation
 */
public interface PutResolver<T> {

    /**
     * Performs Put Operation of some {@link ContentValues} into {@link StorIOContentResolver}
     *
     * @param storIOContentProvider instance of {@link StorIOContentResolver}
     * @param contentValues         some {@link ContentValues} to put
     * @return non-null result of Put Operation
     */
    PutResult performPut(@NonNull StorIOContentResolver storIOContentProvider, @NonNull ContentValues contentValues);

    /**
     * Useful callback which will be called in same thread that performed Put Operation right after
     * execution of {@link #performPut(StorIOContentResolver, ContentValues)}
     * <p>
     * You can, for example, set object's Uri after insert
     *
     * @param object,   that was "put" in {@link StorIOContentResolver}
     * @param putResult result of put operation
     */
    void afterPut(@NonNull T object, @NonNull PutResult putResult);
}
