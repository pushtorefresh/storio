package com.pushtorefresh.storio.contentresolver.impl;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;
import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.Changes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;

import static com.pushtorefresh.storio.test.Utils.MAX_SDK_VERSION;
import static com.pushtorefresh.storio.test.Utils.MIN_SDK_VERSION;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
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
    public void contentObserverShouldReturnFalseOnDeliverSelfNotificationsOnAllSdkVersions() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < MAX_SDK_VERSION; sdkVersion++) {
            ContentResolver contentResolver = mock(ContentResolver.class);
            Uri uri = mock(Uri.class);

            final AtomicReference<ContentObserver> contentObserver = new AtomicReference<ContentObserver>();

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    contentObserver.set((ContentObserver) invocation.getArguments()[2]);
                    return null;
                }
            }).when(contentResolver).registerContentObserver(same(uri), eq(true), any(ContentObserver.class));

            Handler handler = mock(Handler.class);

            Observable<Changes> observable = RxChangesObserver.observeChanges(contentResolver, singleton(uri), handler, sdkVersion);

            Subscription subscription = observable.subscribe();

            assertThat(contentObserver.get().deliverSelfNotifications()).isFalse();

            subscription.unsubscribe();
        }
    }

    @Test
    public void shouldRegisterOnlyOneContentObserverAfterSubscribingToObservableOnSdkVersionGreaterThan15() {
        for (int sdkVersion = 16; sdkVersion < MAX_SDK_VERSION; sdkVersion++) {
            ContentResolver contentResolver = mock(ContentResolver.class);

            final AtomicReference<ContentObserver> contentObserver = new AtomicReference<ContentObserver>();

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    // Save reference to ContentObserver only once to assert that it was created once
                    if (contentObserver.get() == null) {
                        contentObserver.set((ContentObserver) invocation.getArguments()[2]);
                    } else if (contentObserver.get() != invocation.getArguments()[2]) {
                        throw new AssertionError("More than one ContentObserver was created");
                    }
                    return null;
                }
            }).when(contentResolver).registerContentObserver(any(Uri.class), eq(true), any(ContentObserver.class));

            Set<Uri> uris = new HashSet<Uri>(3);
            uris.add(mock(Uri.class));
            uris.add(mock(Uri.class));
            uris.add(mock(Uri.class));

            Observable<Changes> observable = RxChangesObserver
                    .observeChanges(
                            contentResolver,
                            uris,
                            mock(Handler.class),
                            sdkVersion
                    );

            // Should not register ContentObserver before subscribing to Observable
            verify(contentResolver, times(0))
                    .registerContentObserver(any(Uri.class), anyBoolean(), any(ContentObserver.class));

            Subscription subscription = observable.subscribe();

            for (Uri uri : uris) {
                // Assert that same ContentObserver was registered for all uris
                verify(contentResolver).registerContentObserver(same(uri), eq(true), same(contentObserver.get()));
            }

            subscription.unsubscribe();
        }
    }

    @Test
    public void shouldRegisterObserverForEachPassedUriAfterSubscribingToObservableOnSdkVersionLowerThan15() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < 16; sdkVersion++) {
            ContentResolver contentResolver = mock(ContentResolver.class);
            final Map<Uri, ContentObserver> contentObservers = new HashMap<Uri, ContentObserver>(3);

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    contentObservers.put((Uri) invocation.getArguments()[0], (ContentObserver) invocation.getArguments()[2]);
                    return null;
                }
            }).when(contentResolver).registerContentObserver(any(Uri.class), eq(true), any(ContentObserver.class));

            Set<Uri> uris = new HashSet<Uri>(3);
            uris.add(mock(Uri.class));
            uris.add(mock(Uri.class));
            uris.add(mock(Uri.class));

            Observable<Changes> observable = RxChangesObserver.observeChanges(
                    contentResolver,
                    uris,
                    mock(Handler.class),
                    sdkVersion
            );

            // Should not register ContentObserver before subscribing to Observable
            verify(contentResolver, times(0))
                    .registerContentObserver(any(Uri.class), anyBoolean(), any(ContentObserver.class));

            Subscription subscription = observable.subscribe();

            for (Uri uri : uris) {
                // Assert that new ContentObserver was registered for each uri
                verify(contentResolver).registerContentObserver(same(uri), eq(true), same(contentObservers.get(uri)));
            }

            assertThat(contentObservers).hasSameSizeAs(uris);

            subscription.unsubscribe();
        }
    }

    @Test
    public void shouldUnregisterContentObserverAfterUnsubscribingFromObservableOnSdkVersionGreaterThan15() {
        for (int sdkVersion = 16; sdkVersion < MAX_SDK_VERSION; sdkVersion++) {
            ContentResolver contentResolver = mock(ContentResolver.class);
            Set<Uri> uris = new HashSet<Uri>(3);
            uris.add(mock(Uri.class));
            uris.add(mock(Uri.class));
            uris.add(mock(Uri.class));

            Subscription subscription = RxChangesObserver
                    .observeChanges(
                            contentResolver,
                            uris,
                            mock(Handler.class),
                            sdkVersion)
                    .subscribe();

            // Should not unregister before unsubscribe from Subscription
            verify(contentResolver, times(0)).unregisterContentObserver(any(ContentObserver.class));

            subscription.unsubscribe();

            // Should unregister ContentObserver after unsubscribing from Subscription
            verify(contentResolver).unregisterContentObserver(any(ContentObserver.class));
        }
    }

    @Test
    public void shouldUnregisterContentObserversForEachUriAfterUnsubscribingFromObservableOnSdkVersionLowerThan16() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < 16; sdkVersion++) {
            ContentResolver contentResolver = mock(ContentResolver.class);
            Set<Uri> uris = new HashSet<Uri>(3);
            uris.add(mock(Uri.class));
            uris.add(mock(Uri.class));
            uris.add(mock(Uri.class));

            final Map<Uri, ContentObserver> contentObservers = new HashMap<Uri, ContentObserver>(3);

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    contentObservers.put((Uri) invocation.getArguments()[0], (ContentObserver) invocation.getArguments()[2]);
                    return null;
                }
            }).when(contentResolver).registerContentObserver(any(Uri.class), eq(true), any(ContentObserver.class));

            Subscription subscription = RxChangesObserver
                    .observeChanges(
                            contentResolver,
                            uris,
                            mock(Handler.class),
                            sdkVersion)
                    .subscribe();

            // Should not unregister before unsubscribe from Subscription
            verify(contentResolver, times(0)).unregisterContentObserver(any(ContentObserver.class));

            subscription.unsubscribe();

            for (Uri uri : uris) {
                // Assert that ContentObserver for each uri was unregistered
                verify(contentResolver).unregisterContentObserver(contentObservers.get(uri));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Test
    public void shouldEmitChangesOnSdkVersionGreaterThan15() {
        for (int sdkVersion = 16; sdkVersion < MAX_SDK_VERSION; sdkVersion++) {
            ContentResolver contentResolver = mock(ContentResolver.class);
            final AtomicReference<ContentObserver> contentObserver = new AtomicReference<ContentObserver>();

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    // Save reference to ContentObserver only once to assert that it was created once
                    if (contentObserver.get() == null) {
                        contentObserver.set((ContentObserver) invocation.getArguments()[2]);
                    } else if (contentObserver.get() != invocation.getArguments()[2]) {
                        throw new AssertionError("More than one ContentObserver was created");
                    }
                    return null;
                }
            }).when(contentResolver).registerContentObserver(any(Uri.class), eq(true), any(ContentObserver.class));

            TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

            Uri uri1 = mock(Uri.class);
            Uri uri2 = mock(Uri.class);
            Set<Uri> uris = new HashSet<Uri>(2);
            uris.add(uri1);
            uris.add(uri2);

            RxChangesObserver
                    .observeChanges(
                            contentResolver,
                            uris,
                            mock(Handler.class),
                            sdkVersion)
                    .subscribe(testSubscriber);

            testSubscriber.assertNoTerminalEvent();
            testSubscriber.assertNoValues();

            // RxChangesObserver should ignore call to onChange() without Uri on sdkVersion >= 16
            contentObserver.get().onChange(false);
            testSubscriber.assertNoValues();

            // Emulate change of Uris, Observable should react and emit Changes objects
            contentObserver.get().onChange(false, uri1);
            contentObserver.get().onChange(false, uri2);

            testSubscriber.assertValues(Changes.newInstance(uri1), Changes.newInstance(uri2));

            testSubscriber.unsubscribe();
            testSubscriber.assertNoErrors();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Test
    public void shouldEmitChangesOnSdkVersionLowerThan16() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < 16; sdkVersion++) {
            ContentResolver contentResolver = mock(ContentResolver.class);
            final Map<Uri, ContentObserver> contentObservers = new HashMap<Uri, ContentObserver>(3);

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    contentObservers.put((Uri) invocation.getArguments()[0], (ContentObserver) invocation.getArguments()[2]);
                    return null;
                }
            }).when(contentResolver).registerContentObserver(any(Uri.class), eq(true), any(ContentObserver.class));

            TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

            Uri uri1 = mock(Uri.class);
            Uri uri2 = mock(Uri.class);
            Set<Uri> uris = new HashSet<Uri>(2);
            uris.add(uri1);
            uris.add(uri2);

            RxChangesObserver
                    .observeChanges(
                            contentResolver,
                            uris,
                            mock(Handler.class),
                            sdkVersion)
                    .subscribe(testSubscriber);

            testSubscriber.assertNoTerminalEvent();
            testSubscriber.assertNoValues();

            // Emulate change of Uris, Observable should react and emit Changes objects
            contentObservers.get(uri1).onChange(false);
            contentObservers.get(uri2).onChange(false);
            testSubscriber.assertValues(Changes.newInstance(uri1), Changes.newInstance(uri2));

            testSubscriber.unsubscribe();
            testSubscriber.assertNoErrors();
        }
    }
}
