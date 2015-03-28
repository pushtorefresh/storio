package com.pushtorefresh.storio.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.DeleteQuery;

/**
 * Default implementation for {@link DeleteResolver}, thread-safe
 */
public class DefaultDeleteResolver implements DeleteResolver {

    // to prevent unneeded allocations
    static final DefaultDeleteResolver INSTANCE = new DefaultDeleteResolver();

    @Override
    public int performDelete(@NonNull StorIODb storIODb, @NonNull DeleteQuery deleteQuery) {
        return storIODb.internal().delete(deleteQuery);
    }
}
