package com.pushtorefresh.storio3.operations;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import io.reactivex.Completable;

/**
 * Common API of prepared operations with {@link Completable} support
 *
 * @param <Result> type of result
 */
public interface PreparedCompletableOperation<Result, Data> extends PreparedOperation<Result, Result, Data> {

    @NonNull
    @CheckResult
    Completable asRxCompletable();
}
