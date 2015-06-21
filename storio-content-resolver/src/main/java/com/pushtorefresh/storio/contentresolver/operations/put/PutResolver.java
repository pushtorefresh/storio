package com.pushtorefresh.storio.contentresolver.operations.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;

/**
 * Defines behavior of Put Operation (insert or update).
 *
 * @param <T> type of objects to put.
 * @see DefaultPutResolver
 */
public abstract class PutResolver<T> {

    /**
     * Performs Put Operation of some {@link android.content.ContentValues}
     * into {@link StorIOContentResolver}.
     *
     * @param storIOContentResolver instance of {@link StorIOContentResolver}.
     * @param object                non-null object of required type to put.
     * @return non-null result of Put Operation.
     */
    @NonNull
    public abstract PutResult performPut(@NonNull StorIOContentResolver storIOContentResolver, @NonNull T object);
}
