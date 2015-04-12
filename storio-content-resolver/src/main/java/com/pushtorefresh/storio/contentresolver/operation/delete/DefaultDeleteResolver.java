package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;

/**
 * Default implementation of {@link DeleteResolver}
 * <p>
 * Simply redirects {@link DeleteQuery} to {@link StorIOContentResolver}
 * <p>
 * Instances of this class are thread-safe
 */
public class DefaultDeleteResolver implements DeleteResolver {

    /**
     * We can safely share it instead of creating new instance each time
     */
    static final DefaultDeleteResolver INSTANCE = new DefaultDeleteResolver();

    /**
     * Performs Delete Operation
     * <p>
     * Simply redirects {@link DeleteQuery} to {@link StorIOContentResolver}
     *
     * @param storIOContentResolver instance of {@link StorIOContentResolver}
     * @param deleteQuery           query that specifies what should be deleted
     * @return non-null result of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResult performDelete(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteQuery deleteQuery) {
        final int numberOfRowsDeleted = storIOContentResolver.internal().delete(deleteQuery);
        return DeleteResult.newInstance(numberOfRowsDeleted, deleteQuery.uri);
    }
}
