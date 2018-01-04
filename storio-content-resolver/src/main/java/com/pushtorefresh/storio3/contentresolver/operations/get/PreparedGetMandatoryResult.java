package com.pushtorefresh.storio3.contentresolver.operations.get;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.queries.Query;

/**
 * Represents Get Operation for {@link StorIOContentResolver}.
 *
 * @param <Result> type of result.
 */
public abstract class PreparedGetMandatoryResult<Result> extends PreparedGet<Result, Result> {

    PreparedGetMandatoryResult(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Query query) {
        super(storIOContentResolver, query);
    }

    /**
     * {@inheritDoc}
     */
    @WorkerThread
    @NonNull
    public final Result executeAsBlocking() {
        //noinspection ConstantConditions
        return super.executeAsBlocking();
    }
}
