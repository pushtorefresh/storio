package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

/**
 * Default implementation of {@link GetResolver}.
 * <p>
 * Thread-safe.
 * <p>
 * You need to just override mapping from Cursor.
 */
public abstract class DefaultGetResolver<T> extends GetResolver<T> {

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        return storIOSQLite.lowLevel().rawQuery(rawQuery);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        return storIOSQLite.lowLevel().query(query);
    }
}
