package com.pushtorefresh.storio2.internal;

import android.support.annotation.NonNull;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Hides RxJava from ClassLoader...
 */
class RxChangesBus<T> {

    @NonNull
    private final FlowableProcessor<T> rxBus = PublishProcessor.<T>create().toSerialized();

    public void onNext(@NonNull T next) {
        rxBus.onNext(next);
    }

    @NonNull
    public Flowable<T> asFlowable() {
        return rxBus;
    }
}
