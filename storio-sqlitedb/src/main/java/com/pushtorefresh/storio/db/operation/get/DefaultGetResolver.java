package com.pushtorefresh.storio.db.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;

/**
 * Default implementation of {@link GetResolver}, thread-safe
 */
public class DefaultGetResolver implements GetResolver {

    // it's thread safe and we can share it instead of creating new one for each Get operation
    static final DefaultGetResolver INSTANCE = new DefaultGetResolver();

    @NonNull @Override
    public Cursor performGet(@NonNull StorIODb storIODb, @NonNull RawQuery rawQuery) {
        return storIODb.internal().rawQuery(rawQuery);
    }

    @NonNull @Override public Cursor performGet(@NonNull StorIODb storIODb, @NonNull Query query) {
        return storIODb.internal().query(query);
    }
}
