package com.pushtorefresh.storio.test;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.functions.Action1;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ObservableBehaviorCheckerTest {

    @Test
    public void assertThatObservableEmitsOncePositive() {
        final String testString = "Test string";
        final Observable<String> testObservable = Observable.just(testString);
        //noinspection unchecked
        final Action1<String> testAction = mock(Action1.class);

        new ObservableBehaviorChecker<String>()
                .observable(testObservable)
                .expectedNumberOfEmissions(1)
                .testAction(testAction)
                .checkBehaviorOfObservable();

        verify(testAction, times(1)).call(testString);
    }

    @Test(expected = IllegalStateException.class)
    public void assertThatObservableEmitsOnceNegative() {
        final Observable<Integer> testObservable = Observable.just(1, 2);

        new ObservableBehaviorChecker<Integer>()
                .observable(testObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Integer>() {
                    final AtomicInteger numberOfInvocations = new AtomicInteger(0);

                    @Override
                    public void call(Integer i) {
                        if (numberOfInvocations.incrementAndGet() > 1) {
                            fail("Should be called once");
                        }
                    }
                }).checkBehaviorOfObservable();
    }

    @Test
    public void shouldDenyUsingNullObservable() {
        try {
            //noinspection ConstantConditions
            new ObservableBehaviorChecker<Object>()
                    .observable(null)
                    .expectedNumberOfEmissions(1)
                    .testAction(new Action1<Object>() {
                        @Override
                        public void call(Object o) {

                        }
                    }).checkBehaviorOfObservable();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Please specify fields");
        }
    }

    @Test
    public void shouldDenyUsingNullExpectedNumberOfEmissions() {
        try {
            //noinspection ConstantConditions
            new ObservableBehaviorChecker<Object>()
                    .observable(Observable.just(new Object()))
                    .testAction(new Action1<Object>() {
                        @Override
                        public void call(Object o) {

                        }
                    }).checkBehaviorOfObservable();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Please specify fields");
        }
    }

    @Test
    public void shouldDenyUsingNullTestAction() {
        try {
            //noinspection ConstantConditions
            new ObservableBehaviorChecker<Object>()
                    .observable(Observable.just(new Object()))
                    .expectedNumberOfEmissions(1)
                    .checkBehaviorOfObservable();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Please specify fields");
        }
    }
}
