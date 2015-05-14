package com.pushtorefresh.storio.internal;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Hides RxJava from ClassLoader...
 */
class RxChangesBus<T> {

    @NonNull
    private final Subject<T, T> rxBus = PublishSubject.<T>create().toSerialized();

    public void onNext(@NonNull T next) {
        rxBus.onNext(next);
    }

    @NonNull
    public Observable<T> asObservable() {
        return rxBus;
    }
}
