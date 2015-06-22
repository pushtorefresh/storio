package com.pushtorefresh.storio.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Environment.RX_JAVA_IS_AVAILABLE;

/**
 * FOR INTERNAL USAGE ONLY.
 * <p>
 * Thread-safe changes bus.
 */
public final class ChangesBus<T> {

    @Nullable
    private final RxChangesBus<T> rxChangesBus = RX_JAVA_IS_AVAILABLE
            ? new RxChangesBus<T>()
            : null;

    public void onNext(@NonNull T next) {
        if (rxChangesBus != null) {
            rxChangesBus.onNext(next);
        }
    }

    @Nullable
    public Observable<T> asObservable() {
        return rxChangesBus != null
                ? rxChangesBus.asObservable()
                : null;
    }
}
