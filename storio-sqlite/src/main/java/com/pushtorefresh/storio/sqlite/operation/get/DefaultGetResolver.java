package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

/**
 * Default implementation of {@link GetResolver}, thread-safe
 */
public class DefaultGetResolver implements GetResolver {

    // it's thread safe and we can share it instead of creating new one for each Get operation
    static final DefaultGetResolver INSTANCE = new DefaultGetResolver();

    @NonNull @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        return storIOSQLite.internal().rawQuery(rawQuery);
    }

    @NonNull @Override public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        return storIOSQLite.internal().query(query);
    }
}
