package com.pushtorefresh.storio.contentprovider.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;
import com.pushtorefresh.storio.contentprovider.query.Query;

/**
 * Default implementation of {@link GetResolver}, thread-safe
 */
public class DefaultGetResolver implements GetResolver {

    // it's thread safe and we can share it instead of creating new one for each Get operation
    static final DefaultGetResolver INSTANCE = new DefaultGetResolver();

    @Nullable
    @Override
    public Cursor performGet(@NonNull StorIOContentResolver storIOContentProvider, @NonNull Query query) {
        return storIOContentProvider.internal().query(query);
    }
}
