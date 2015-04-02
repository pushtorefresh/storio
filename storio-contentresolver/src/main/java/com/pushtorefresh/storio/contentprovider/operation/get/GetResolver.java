package com.pushtorefresh.storio.contentprovider.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;
import com.pushtorefresh.storio.contentprovider.query.Query;

/**
 * Resolves Get operation, you can make your own implementation with caching and other stuff!
 */
public interface GetResolver {

    @Nullable
    Cursor performGet(@NonNull StorIOContentResolver storIOContentProvider, @NonNull Query query);
}
