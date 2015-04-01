package com.pushtorefresh.storio.test;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.functions.Action1;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ObservableBehaviorCheckerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void assertThatObservableEmitsOncePositive() {
        final String testString = "Test string";
        final Observable<String> testObservable = Observable.just(testString);
        final Action1<String> testAction = mock(Action1.class);

        new ObservableBehaviorChecker()
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
}
