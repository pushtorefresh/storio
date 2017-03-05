package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operations.PreparedOperation;

public interface Interceptor {

    @Nullable
    <Result> Result intercept(@NonNull PreparedOperation<Result> operation, @NonNull Chain chain);

    interface Chain {

        @Nullable
        <Result> Result proceed(@NonNull PreparedOperation<Result> operation);
    }
}