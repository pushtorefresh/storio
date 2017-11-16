package com.pushtorefresh.storio2.contentresolver.operations.get;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.queries.Query;

/**
 * Represents Get Operation for {@link StorIOContentResolver}.
 *
 * @param <Result> type of result.
 */
public abstract class PreparedGetMandatoryResult<Result> extends PreparedGet<Result, Result> {

    PreparedGetMandatoryResult(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Query query) {
        super(storIOContentResolver, query);
    }
}
