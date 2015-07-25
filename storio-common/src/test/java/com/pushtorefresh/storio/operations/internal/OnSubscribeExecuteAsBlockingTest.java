package com.pushtorefresh.storio.operations.internal;

import com.pushtorefresh.storio.operations.PreparedOperation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.Subscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Subscriber.class)
public class OnSubscribeExecuteAsBlockingTest {

    @Test
    public void shouldExecuteAsBlockingAfterSubscription() {
        //noinspection unchecked
        final PreparedOperation<String> preparedOperation = mock(PreparedOperation.class);

        final String expectedResult = "expected_string";

        when(preparedOperation.executeAsBlocking())
                .thenReturn(expectedResult);

        final String actualResult = Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(preparedOperation))
                .toBlocking()
                .first();

        verify(preparedOperation, times(1)).executeAsBlocking();
        verify(preparedOperation, times(0)).createObservable();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldNotNotifySubscriberIfItsAlreadyUnsubscribed() {
        //noinspection unchecked
        PreparedOperation<Object> preparedOperation = mock(PreparedOperation.class);

        //noinspection unchecked
        Subscriber<Object> subscriber = PowerMockito.mock(Subscriber.class);

        // subscriber.isUnsubscribed() is final, so we use PowerMock to override it
        PowerMockito.when(subscriber.isUnsubscribed()).thenReturn(true);

        OnSubscribeExecuteAsBlocking
                .newInstance(preparedOperation)
                .call(subscriber);

        verify(preparedOperation).executeAsBlocking();

        // Only call to isUnsubscribed() should be done
        verify(subscriber).isUnsubscribed();

        // Subscriber should not be notified about results of calculations
        PowerMockito.verifyNoMoreInteractions(subscriber);
    }

    @Test
    public void shouldCallExecuteAsBlockingEvenIfSubscriberAlreadyUnsubscribed() {
        //noinspection unchecked
        PreparedOperation<Object> preparedOperation = mock(PreparedOperation.class);

        //noinspection unchecked
        Subscriber<Object> subscriber = PowerMockito.mock(Subscriber.class);

        // subscriber.isUnsubscribed() is final, so we use PowerMock to override it
        PowerMockito.when(subscriber.isUnsubscribed()).thenReturn(true);

        OnSubscribeExecuteAsBlocking
                .newInstance(preparedOperation)
                .call(subscriber);

        // Even if subscriber is unsubscribed when call was done,
        // executeAsBlocking() must be called (for example for Put and Delete operations)
        // But we should think about skipping call to executeAsBlocking() for Get Operation in same case
        verify(preparedOperation).executeAsBlocking();
    }
}
