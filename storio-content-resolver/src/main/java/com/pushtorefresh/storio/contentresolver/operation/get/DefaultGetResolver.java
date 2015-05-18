package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;
import android.database.MatrixCursor;
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
        final Cursor cursor = storIOContentResolver.internal().query(query);

        if (cursor == null) {
            return new MatrixCursor(new String[]{}, 0);
        } else {
            return cursor;
        }
    }
}
