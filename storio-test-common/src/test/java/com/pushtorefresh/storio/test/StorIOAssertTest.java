package com.pushtorefresh.storio.test;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.functions.Action1;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StorIOAssertTest {

    @SuppressWarnings("unchecked")
    @Test
    public void assertThatObservableEmitsOncePositive() {
        final Action1<String> testAction = mock(Action1.class);
        final String testString = "Test string";
        StorIOAssert.assertThatObservableEmitsOnce(Observable.just(testString), testAction);
        verify(testAction, times(1)).call(testString);
    }

    @Test(expected = IllegalStateException.class)
    public void assertThatObservableEmitsOnceNegative() {
        StorIOAssert.assertThatObservableEmitsOnce(Observable.just(1, 2), new Action1<Integer>() {
            final AtomicInteger numberOfInvocations = new AtomicInteger(0);

            @Override
            public void call(Integer integer) {
                if (numberOfInvocations.incrementAndGet() > 1) {
                    fail("Should be called once");
                }
            }
        });
    }
}
