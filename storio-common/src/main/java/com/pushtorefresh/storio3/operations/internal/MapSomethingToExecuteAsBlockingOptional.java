package com.pushtorefresh.storio3.operations.internal;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import io.reactivex.functions.Function;

/**
 * Required to avoid problems with ClassLoader when RxJava is not in ClassPath
 * We can not use anonymous classes from RxJava directly in StorIO, ClassLoader won't be happy :(
 * <p>
 * For internal usage only!
 */
public final class MapSomethingToExecuteAsBlockingOptional<Something, Result, Data> implements Function<Something, Optional<Result>> {

    @NonNull
    private final PreparedOperation<Result, Optional<Result>, Data> preparedOperation;

    public MapSomethingToExecuteAsBlockingOptional(@NonNull PreparedOperation<Result, Optional<Result>, Data> preparedOperation) {
        this.preparedOperation = preparedOperation;
    }

    @Override
    @NonNull
    public Optional<Result> apply(@NonNull Something something) throws Exception {
        final Result value = preparedOperation.executeAsBlocking();
        return Optional.of(value);
    }
}
