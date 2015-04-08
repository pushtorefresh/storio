package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

/**
 * Default implementation for {@link DeleteResolver}, thread-safe
 */
public class DefaultDeleteResolver implements DeleteResolver {

    // to prevent unneeded allocations
    static final DefaultDeleteResolver INSTANCE = new DefaultDeleteResolver();

    @Override
    public int performDelete(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull DeleteQuery deleteQuery) {
        return storIOSQLiteDb.internal().delete(deleteQuery);
    }
}
