package com.pushtorefresh.storio2.contentresolver.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.queries.Query;

/**
 * Default implementation of {@link GetResolver}, thread-safe.
 */
public abstract class DefaultGetResolver<T> extends GetResolver<T> {

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Query query) {
        return storIOContentResolver.lowLevel().query(query);
    }
}
