package com.pushtorefresh.storio.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.DeleteQuery;

/**
 * Resolves Delete operation
 */
public interface DeleteResolver {

    /**
     * Performs delete operation
     *
     * @param storIODb    {@link StorIODb} instance to perform delete on
     * @param deleteQuery delete query
     * @return number of deleted rows
     */
    int performDelete(@NonNull StorIODb storIODb, @NonNull DeleteQuery deleteQuery);
}
