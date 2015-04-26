package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;

/**
 * Defines behavior of Put Operation (insert or update)
 *
 * @param <T> type of objects to put
 * @see {@link DefaultPutResolver} default implementation
 */
public abstract class PutResolver<T> {

    /**
     * Performs put of an object
     *
     * @param storIOSQLite {@link StorIOSQLite} instance to perform put into
     * @param object       non-null object that should be put into {@link StorIOSQLite}
     * @return non-null result of Put Operation
     */
    @NonNull
    public abstract PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull T object);
}
