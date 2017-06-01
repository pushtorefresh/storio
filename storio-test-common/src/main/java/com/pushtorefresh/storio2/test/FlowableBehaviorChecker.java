package com.pushtorefresh.storio2.test;

import android.support.annotation.NonNull;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Helps with checking behavior of {@link Flowable}
 *
 * @param <T> type of emission of the {@link Flowable}
 */
public class FlowableBehaviorChecker<T> {

    private Flowable<T> flowable;
    private Integer expectedNumberOfEmissions;
    private Consumer<T> testAction;

    /**
     * Required: Specifies {@link Flowable} for the check
     * <p>
     * Default value is <code>null</code>
     *
     * @param flowable flowable
     * @return checker
     */
    @NonNull
    public FlowableBehaviorChecker<T> flowable(@NonNull Flowable<T> flowable) {
        this.flowable = flowable;
        return this;
    }

    /**
     * Required: Specifies expected number of emissions of the {@link Flowable}
     * <p>
     * If {@link Flowable} will emit more or less items, check will fail
     * <p>
     * Default value is <code>null</code>
     *
     * @param expectedNumberOfEmission expected number of emissions
     * @return checker
     */
    @NonNull
    public FlowableBehaviorChecker<T> expectedNumberOfEmissions(int expectedNumberOfEmission) {
        this.expectedNumberOfEmissions = expectedNumberOfEmission;
        return this;
    }

    /**
     * Required: Specifies test action which will be applied to each emission of the {@link Flowable}
     * <p>
     * Action may throw an exception if emissions is not expected, etc
     * <p>
     * Default value is <code>null</code>
     *
     * @param testAction test action which will be applied to each emission of the {@link Flowable}
     * @return checker
     */
    @NonNull
    public FlowableBehaviorChecker<T> testAction(@NonNull Consumer<T> testAction) {
        this.testAction = testAction;
        return this;
    }

    /**
     * Checks that behavior of the {@link Flowable} is good
     */
    public void checkBehaviorOfFlowable() {
        if (flowable == null || expectedNumberOfEmissions == null || testAction == null) {
            throw new NullPointerException("Please specify fields");
        }

        final Iterable<T> iterableEmission = flowable.blockingIterable();

        int numberOfEmissions = 0;

        for (T emission : iterableEmission) {
            numberOfEmissions++;

            if (numberOfEmissions > expectedNumberOfEmissions) {
                throw new IllegalStateException("Flowable should emit result once");
            }

            try {
                testAction.accept(emission);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
