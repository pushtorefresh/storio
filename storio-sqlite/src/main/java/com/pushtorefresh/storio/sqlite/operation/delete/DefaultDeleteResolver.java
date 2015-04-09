package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

/**
 * Default implementation for {@link DeleteResolver}, thread-safe
 */
public class DefaultDeleteResolver implements DeleteResolver {

    // shared instance for internal usage
    static final DefaultDeleteResolver INSTANCE = new DefaultDeleteResolver();

    @Override
    @NonNull
    public DeleteResult performDelete(@NonNull StorIOSQLite storIOSQLite, @NonNull DeleteQuery deleteQuery) {
        return DeleteResult.newInstance(storIOSQLite.internal().delete(deleteQuery), deleteQuery.table);
    }
}
