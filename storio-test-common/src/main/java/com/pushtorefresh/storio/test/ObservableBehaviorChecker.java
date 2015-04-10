package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Action1;

/**
 * Helps with checking behavior of {@link Observable}
 *
 * @param <T> type of emission of the {@link Observable}
 */
public class ObservableBehaviorChecker<T> {

    private Observable<T> observable;
    private Integer expectedNumberOfEmissions;
    private Action1<T> testAction;

    /**
     * Required: Specifies {@link Observable} for the check
     * <p>
     * Default value is <code>null</code>
     *
     * @param observable observable
     * @return checker
     */
    @NonNull
    public ObservableBehaviorChecker<T> observable(@NonNull Observable<T> observable) {
        this.observable = observable;
        return this;
    }

    /**
     * Required: Specifies expected number of emissions of the {@link Observable}
     * <p>
     * If {@link Observable} will emit more or less items, check will fail
     * <p>
     * Default value is <code>null</code>
     *
     * @param expectedNumberOfEmission expected number of emissions
     * @return checker
     */
    @NonNull
    public ObservableBehaviorChecker<T> expectedNumberOfEmissions(int expectedNumberOfEmission) {
        this.expectedNumberOfEmissions = expectedNumberOfEmission;
        return this;
    }

    /**
     * Required: Specifies test action which will be applied to each emission of the {@link Observable}
     * <p>
     * Action may throw an exception if emissions is not expected, etc
     * <p>
     * Default value is <code>null</code>
     *
     * @param testAction test action which will be applied to each emission of the {@link Observable}
     * @return checker
     */
    @NonNull
    public ObservableBehaviorChecker<T> testAction(@NonNull Action1<T> testAction) {
        this.testAction = testAction;
        return this;
    }

    /**
     * Checks that behavior of the {@link Observable} is good
     */
    public void checkBehaviorOfObservable() {
        if (observable == null || expectedNumberOfEmissions == null || testAction == null) {
            throw new NullPointerException("Please specify fields");
        }

        final Iterable<T> iterableEmission = observable
                .toBlocking()
                .toIterable();

        int numberOfEmissions = 0;

        for (T emission : iterableEmission) {
            numberOfEmissions++;

            if (numberOfEmissions > expectedNumberOfEmissions) {
                throw new IllegalStateException("Observable should emit result once");
            }

            testAction.call(emission);
        }
    }
}
