package com.pushtorefresh.storio.sqlite.operations.get;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedGetListOfObjectsTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldGetListOfObjectsByQueryWithoutTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final List<TestItem> testItems = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyQueryBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsByQueryWithoutTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyQueryBehavior(testItemsObservable);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithoutTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final List<TestItem> testItems = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyRawQueryBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithoutTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyRawQueryBehavior(testItemsObservable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldGetListOfObjectsByQueryWithTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final List<TestItem> testItems = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyQueryBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsByQueryWithTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyQueryBehavior(testItemsObservable);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final List<TestItem> testItems = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyRawQueryBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyRawQueryBehavior(testItemsObservable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final PreparedGet<List<TestItem>> preparedGet = storIOSQLite
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

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final PreparedGet<List<TestItem>> preparedGet = storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(mock(RawQuery.class))
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                fail();
            } catch (IllegalStateException expected) {
                // it's okay, no type mapping was found
            }

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @SuppressWarnings("unchecked")
        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);
            when(storIOSQLite.observeChangesInTables(any(Set.class)))
                    .thenReturn(Observable.empty());

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(mock(Query.class))
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(IllegalStateException.class);

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verify(storIOSQLite).observeChangesInTables(anySet());
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(mock(RawQuery.class))
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(IllegalStateException.class);

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }

}
