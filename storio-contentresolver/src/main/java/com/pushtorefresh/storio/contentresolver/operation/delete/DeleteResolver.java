package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;

/**
 * Defines behavior Delete Operation behavior
 */
public interface DeleteResolver {

    /**
     * Performs Delete Operation
     *
     * @param storIOContentResolver instance of {@link StorIOContentResolver}
     * @param deleteQuery           query that specifies what should be deleted
     * @return non-null result of Delete Operation
     */
    @NonNull
    DeleteResult performDelete(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteQuery deleteQuery);
}
