package com.pushtorefresh.storio.sqlitedb.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.query.DeleteQuery;

/**
 * Resolves Delete operation
 */
public interface DeleteResolver {

    /**
     * Performs delete operation
     *
     * @param storIOSQLiteDb    {@link StorIOSQLiteDb} instance to perform delete on
     * @param deleteQuery delete query
     * @return number of deleted rows
     */
    int performDelete(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull DeleteQuery deleteQuery);
}
