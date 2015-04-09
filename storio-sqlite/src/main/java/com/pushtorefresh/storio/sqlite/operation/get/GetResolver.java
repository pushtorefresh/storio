package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

/**
 * Resolves Get operation, you can make your own implementation with caching and other stuff!
 */
public interface GetResolver {

    @NonNull
    Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery);

    @NonNull
    Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query);
}
