package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

/**
 * Defines behavior of Get Operation.
 */
public abstract class GetResolver<T> {

    /**
     * Converts {@link Cursor} with already set position to object of required type.
     *
     * @param cursor not closed {@link Cursor} with already set position
     *               that should be parsed and converted to object of required type.
     * @return non-null object of required type with data parsed from passed {@link Cursor}.
     */
    @NonNull
    public abstract T mapFromCursor(@NonNull Cursor cursor);

    /**
     * Performs Get Operation
     *
     * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}.
     * @param query                 non-null query that should be processed.
     * @return non-null, not closed {@link Cursor} that can be empty or contain
     * data with results of Get Operation.
     * <p>
     * Notice, that {@link android.content.ContentProvider} can return null {@link Cursor}
     * from {@link android.content.ContentProvider#query(android.net.Uri, String[], String, String[], String)}
     * But null {@link Cursor} is not very great thing to work with,
     * so if {@link android.content.ContentProvider} returns null,
     * {@link StorIOContentResolver} will return empty cursor.
     */
    @NonNull
    public abstract Cursor performGet(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Query query);
}
