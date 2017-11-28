package com.pushtorefresh.storio3.operations.internal;

import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import org.junit.Test;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.MISSING;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FlowableOnSubscribeExecuteAsBlockingOptionalTest {

    @SuppressWarnings("CheckResult")
    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        //noinspection unchecked
        final PreparedOperation<String, Optional<String>, String> preparedOperation = mock(PreparedOperation.class);

        final String expectedResult = "expected_string";
        when(preparedOperation.executeAsBlocking()).thenReturn(expectedResult);

        TestSubscriber<Optional<String>> testSubscriber = new TestSubscriber<Optional<String>>();

        Flowable
                .create(new FlowableOnSubscribeExecuteAsBlockingOptional<String, String>(preparedOperation), MISSING)
                .subscribe(testSubscriber);

        verify(preparedOperation).executeAsBlocking();

        testSubscriber.assertValue(Optional.of(expectedResult));
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
    }

    @Test
    public void shouldCallOnError() {
        Throwable throwable = new IllegalStateException("Test exception");
        //noinspection unchecked
        PreparedOperation<String, Optional<String>, String> preparedOperation = mock(PreparedOperation.class);
        when(preparedOperation.executeAsBlocking()).thenThrow(throwable);

        TestSubscriber<Optional<String>> testSubscriber = TestSubscriber.create();

        Flowable
                .create(new FlowableOnSubscribeExecuteAsBlockingOptional<String, String>(preparedOperation), MISSING)
                .subscribe(testSubscriber);

        testSubscriber.assertError(throwable);
        testSubscriber.assertNoValues();
        testSubscriber.assertNotComplete();
    }
}
