package com.pushtorefresh.storio2.operations.internal;

import com.pushtorefresh.storio2.operations.PreparedOperation;

import org.junit.Test;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FlowableOnSubscribeExecuteAsBlockingTest {

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        //noinspection unchecked
        final PreparedOperation<String, String> preparedOperation = mock(PreparedOperation.class);

        final String expectedResult = "expected_string";
        when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);

        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();

        Flowable
                .create(new FlowableOnSubscribeExecuteAsBlocking<String, String>(preparedOperation), BackpressureStrategy.MISSING)
                .subscribe(testSubscriber);

        verify(preparedOperation).executeAsBlocking();

        testSubscriber.assertValue(expectedResult);
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }

    @Test
    public void shouldCallOnError() {
        Throwable throwable = new IllegalStateException("Test exception");
        //noinspection unchecked
        PreparedOperation<String, String> preparedOperation = mock(PreparedOperation.class);
        when(preparedOperation.executeAsBlocking()).thenThrow(throwable);

        TestSubscriber<String> testSubscriber = TestSubscriber.create();

        Flowable
                .create(new FlowableOnSubscribeExecuteAsBlocking<String, String>(preparedOperation), BackpressureStrategy.MISSING)
                .subscribe(testSubscriber);

        testSubscriber.assertError(throwable);
        testSubscriber.assertNoValues();
        testSubscriber.assertNotComplete();
    }
}
