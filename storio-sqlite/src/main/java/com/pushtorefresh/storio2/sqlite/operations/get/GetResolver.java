package com.pushtorefresh.storio2.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

/**
 * Defines behavior of Get Operation.
 * <p>
 * Implementation should be thread-safe!
 */
public abstract class GetResolver<T> {

    /**
     * Converts {@link Cursor} with already set position to object of required type.
     *
     * @param storIOSQLite {@link StorIOSQLite} instance to perform get from.
     * @param cursor not closed {@link Cursor} with already set position
     *               that should be parsed and converted to object of required type.
     * @return non-null object of required type with data parsed from passed {@link Cursor}.
     */
    @NonNull
    public abstract T mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor);

    /**
     * Performs get of results with passed query.
     *
     * @param storIOSQLite {@link StorIOSQLite} instance to perform get from.
     * @param rawQuery     query that should be performed.
     * @return non-null, not closed {@link Cursor} that can be empty
     * or contain data with results of Get Operation.
     */
    @NonNull
    public abstract Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery);

    /**
     * Performs get of results with passed query.
     *
     * @param storIOSQLite {@link StorIOSQLite} instance to perform get from.
     * @param query        query that should be performed.
     * @return non-null, not closed {@link Cursor} that can be empty
     * or contain data with results of Get Operation.
     */
    @NonNull
    public abstract Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query);
}
