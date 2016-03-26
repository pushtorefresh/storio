package com.pushtorefresh.storio.operations;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import rx.Completable;

/**
 * Common API of prepared write operations
 *
 * @param <Result> type of result
 */
public interface PreparedWriteOperation<Result> extends PreparedOperation<Result> {

    @NonNull
    @CheckResult
    Completable asRxCompletable();
}
