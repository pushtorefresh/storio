package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

/**
 * Resolves Get operation, you can make your own implementation with caching and other stuff!
 */
public interface GetResolver {

    @NonNull
    Cursor performGet(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull RawQuery rawQuery);

    @NonNull
    Cursor performGet(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull Query query);
}
