package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;

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
        return storIOContentResolver.internal().query(query);
    }
}
