package com.pushtorefresh.storio2.sqlite.operations.get;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

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
}
