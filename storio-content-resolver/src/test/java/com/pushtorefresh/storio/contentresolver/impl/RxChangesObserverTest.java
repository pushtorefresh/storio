package com.pushtorefresh.storio.contentresolver.impl;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.test.PrivateConstructorChecker;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Subscription;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RxChangesObserverTest {

    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker
                .forClass(RxChangesObserver.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("No instances please.")
                .check();
    }

    @Test
    public void contentObserverShouldReturnFalseOnDeliverSelfNotifications() {
        ContentResolver contentResolver = mock(ContentResolver.class);
        Set<Uri> uris = singleton(mock(Uri.class));

        final AtomicReference<ContentObserver> contentObserver = new AtomicReference<ContentObserver>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                contentObserver.set((ContentObserver) invocation.getArguments()[2]);
                return null;
            }
        }).when(contentResolver)
                .registerContentObserver(any(Uri.class), eq(true), any(ContentObserver.class));


        Handler handler = mock(Handler.class);

        Observable<Changes> observable = RxChangesObserver.observeChanges(contentResolver, uris, handler);

        Subscription subscription = observable.subscribe();

        assertFalse(contentObserver.get().deliverSelfNotifications());

        subscription.unsubscribe();
    }

    @Test
    public void shouldRegisterContentObserverAfterSubscribingToObservable() {
        ContentResolver contentResolver = mock(ContentResolver.class);

        Observable<Changes> observable = RxChangesObserver
                .observeChanges(
                        contentResolver,
                        singleton(mock(Uri.class)),
                        mock(Handler.class)
                );

        // Should not register ContentObserver before subscribing to Observable
        verify(contentResolver, times(0))
                .registerContentObserver(any(Uri.class), anyBoolean(), any(ContentObserver.class));

        Subscription subscription = observable.subscribe();

        // Should register Content Observer after subscribing to Observable
        verify(contentResolver)
                .registerContentObserver(any(Uri.class), anyBoolean(), any(ContentObserver.class));

        subscription.unsubscribe();
    }

    @Test
    public void shouldUnregisterContentObserverAfterUnsubscribingFromObservable() {
        ContentResolver contentResolver = mock(ContentResolver.class);

        Subscription subscription = RxChangesObserver
                .observeChanges(
                        contentResolver,
                        singleton(mock(Uri.class)),
                        mock(Handler.class))
                .subscribe();

        // Should not unregister before unsubscribe from Subscription
        verify(contentResolver, times(0)).unregisterContentObserver(any(ContentObserver.class));

        subscription.unsubscribe();

        // Should unregister ContentObserver after unsubscribing from Subscription
        verify(contentResolver).unregisterContentObserver(any(ContentObserver.class));
    }

    @Test
    public void shouldRegisterContentObserverForPassedUris() {
        Set<Uri> uris = new HashSet<Uri>();

        // All Uris are different (objects)
        uris.add(mock(Uri.class));
        uris.add(mock(Uri.class));
        uris.add(mock(Uri.class));

        ContentResolver contentResolver = mock(ContentResolver.class);

        Observable<Changes> observable = RxChangesObserver
                .observeChanges(
                        contentResolver,
                        uris,
                        mock(Handler.class)
                );

        // Should not register ContentObserver before subscribing to Observable
        verify(contentResolver, times(0))
                .registerContentObserver(any(Uri.class), anyBoolean(), any(ContentObserver.class));

        Subscription subscription = observable.subscribe();

        // Should register ContentObserver for each uri from passed set of uris
        for (Uri uri : uris) {
            verify(contentResolver).registerContentObserver(eq(uri), anyBoolean(), any(ContentObserver.class));
        }

        subscription.unsubscribe();
    }
}
