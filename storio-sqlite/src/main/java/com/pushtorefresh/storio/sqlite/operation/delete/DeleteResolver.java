package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

/**
 * Defines behavior of Delete Operation
 */
public interface DeleteResolver {

    /**
     * Performs delete operation
     *
     * @param storIOSQLiteDb    {@link StorIOSQLite} instance to perform delete on
     * @param deleteQuery delete query
     * @return number of deleted rows
     */
    int performDelete(@NonNull StorIOSQLite storIOSQLiteDb, @NonNull DeleteQuery deleteQuery);
}
