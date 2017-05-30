package com.pushtorefresh.storio2.operations;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import io.reactivex.Completable;

/**
 * Common API of prepared write operations
 *
 * @param <Result> type of result
 */
public interface PreparedWriteOperation<Result, Data> extends PreparedOperation<Result, Data> {

    @NonNull
    @CheckResult
    Completable asRxCompletable();
}
