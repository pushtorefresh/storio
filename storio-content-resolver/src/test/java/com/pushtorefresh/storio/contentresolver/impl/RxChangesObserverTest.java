package com.pushtorefresh.storio.contentresolver.impl;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.pushtorefresh.storio.contentresolver.Changes;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.Subscription;

import static java.util.Collections.singleton;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RxChangesObserverTest {

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

        // Should not unregister before unsubscibe from Subscription
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
    }
}
