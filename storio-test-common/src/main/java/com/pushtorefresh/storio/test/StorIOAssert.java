package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Action1;

public final class StorIOAssert {

    private StorIOAssert() {
        throw new IllegalStateException("No instances please");
    }

    /**
     * Asserts that Observable emits item once and applies test action to the emission
     *
     * @param observable observable to test
     * @param testAction action to perform on the emission
     * @param <T>        type of emission
     */
    public static <T> void assertThatObservableEmitsOnce(@NonNull Observable<T> observable, @NonNull Action1<T> testAction) {
        final Iterable<T> iterableEmission = observable
                .toBlocking()
                .toIterable();

        int numberOfEmissions = 0;

        for (T emission : iterableEmission) {
            numberOfEmissions++;

            if (numberOfEmissions > 1) {
                throw new IllegalStateException("Observable should emit result once");
            }

            testAction.call(emission);
        }
    }
}
