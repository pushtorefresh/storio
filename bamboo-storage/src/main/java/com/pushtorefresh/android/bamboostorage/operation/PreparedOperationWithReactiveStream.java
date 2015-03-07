package com.pushtorefresh.android.bamboostorage.operation;

import android.support.annotation.NonNull;

import rx.Observable;

public interface PreparedOperationWithReactiveStream<Result> extends PreparedOperation<Result> {

    @NonNull Observable<Result> createObservableStream();
}
