package com.pushtorefresh.storio.contentresolver.operations.get;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedGetListOfObjectsTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldGetListOfObjectsWithoutTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithoutTypeMapping();

            final List<TestItem> testItems = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsWithoutTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithoutTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyBehavior(testItemsObservable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldGetListOfObjectsWithTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithTypeMapping();

            final List<TestItem> testItems = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsWithTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyBehavior(testItemsObservable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

            when(storIOContentResolver.internal()).thenReturn(internal);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            final PreparedGet<List<TestItem>> preparedGet = storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(mock(Query.class))
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                fail();
            } catch (IllegalStateException expected) {
                // it's okay, no type mapping was found
            }

            verify(storIOContentResolver).get();
            verify(storIOContentResolver).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verifyNoMoreInteractions(storIOContentResolver, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderAsObservable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.Internal internal = mock(StorIOContentResolver.Internal.class);

            when(storIOContentResolver.internal()).thenReturn(internal);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            when(storIOContentResolver.observeChangesOfUri(any(Uri.class)))
                    .thenReturn(Observable.<Changes>empty());

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(mock(Query.class))
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(IllegalStateException.class);

            verify(storIOContentResolver).get();
            verify(storIOContentResolver).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verify(storIOContentResolver).observeChangesOfUri(any(Uri.class));

            verifyNoMoreInteractions(storIOContentResolver, internal);
        }
    }
}
