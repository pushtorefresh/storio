package com.pushtorefresh.storio2.operations;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import io.reactivex.Maybe;

/**
 * Common API of prepared operations with {@link Maybe} support
 *
 * @param <Result> type of result
 */
public interface PreparedMaybeOperation<Result, WrappedResult, Data> extends PreparedOperation<Result, WrappedResult, Data> {

    @NonNull
    @CheckResult
    Maybe<Result> asRxMaybe();
}
