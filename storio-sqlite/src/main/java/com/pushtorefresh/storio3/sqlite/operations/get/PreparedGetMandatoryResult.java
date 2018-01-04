package com.pushtorefresh.storio3.sqlite.operations.get;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;

/**
 * Prepared Get Operation for {@link StorIOSQLite}.
 *
 * @param <Result> type of result.
 */
public abstract class PreparedGetMandatoryResult<Result> extends PreparedGet<Result, Result> {

    PreparedGetMandatoryResult(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        super(storIOSQLite, query);
    }

    PreparedGetMandatoryResult(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        super(storIOSQLite, rawQuery);
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
