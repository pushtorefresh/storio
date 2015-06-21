package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;

/**
 * Defines behavior Delete Operation behavior.
 */
public abstract class DeleteResolver<T> {

    /**
     * Performs Delete Operation.
     *
     * @param storIOContentResolver instance of {@link StorIOContentResolver}.
     * @param object                non-null object that should be deleted.
     * @return non-null result of Delete Operation.
     */
    @NonNull
    public abstract DeleteResult performDelete(@NonNull StorIOContentResolver storIOContentResolver, @NonNull T object);
}
