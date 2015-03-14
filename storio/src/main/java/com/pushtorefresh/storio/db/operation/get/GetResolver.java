package com.pushtorefresh.storio.db.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;

/**
 * Resolves Get operation, you can make your own implementation with caching and other stuff!
 */
public interface GetResolver {

    @NonNull Cursor performGet(@NonNull StorIODb storIODb, @NonNull RawQuery rawQuery);

    @NonNull Cursor performGet(@NonNull StorIODb storIODb, @NonNull Query query);
}
