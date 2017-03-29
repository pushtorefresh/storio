package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
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
                    .asRxObservable()
                    .take(1);

            getStub.verifyQueryBehavior(testItemsObservable);
        }

        @Test
        public void shouldGetListOfObjectsByQueryWithoutTypeMappingAsSingle() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final Single<List<TestItem>> testItemsSingle = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .asRxSingle();

            getStub.verifyQueryBehavior(testItemsSingle);
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
                    .asRxObservable()
                    .take(1);

            getStub.verifyRawQueryBehavior(testItemsObservable);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithoutTypeMappingAsSingle() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final Single<List<TestItem>> testItemsSingle = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .asRxSingle();

            getStub.verifyRawQueryBehavior(testItemsSingle);
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
                    .asRxObservable()
                    .take(1);

            getStub.verifyQueryBehavior(testItemsObservable);
        }

        @Test
        public void shouldGetListOfObjectsByQueryWithTypeMappingAsSingle() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final Single<List<TestItem>> testItemsSingle = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .asRxSingle();

            getStub.verifyQueryBehavior(testItemsSingle);
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
                    .asRxObservable()
                    .take(1);

            getStub.verifyRawQueryBehavior(testItemsObservable);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithTypeMappingAsSingle() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final Single<List<TestItem>> testItemsSingle = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .prepare()
                    .asRxSingle();

            getStub.verifyRawQueryBehavior(testItemsSingle);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final PreparedGet<List<TestItem>> preparedGet = storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(Query.builder().table("test_table").build())
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOSQLite).get();
            verify(storIOSQLite).lowLevel();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final PreparedGet<List<TestItem>> preparedGet = storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(RawQuery.builder().query("test query").build())
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOSQLite).get();
            verify(storIOSQLite).lowLevel();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @SuppressWarnings("unchecked")
        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
            when(storIOSQLite.observeChanges()).thenReturn(Observable.<Changes>empty());

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(Query.builder().table("test_table").observesTags("test_tag").build())
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).get();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).query(any(Query.class));
            verify(storIOSQLite).observeChanges();
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(RawQuery.builder().query("test query").build())
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).get();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @SuppressWarnings("unchecked")
        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(Query.builder().table("test_table").build())
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).get();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.lowLevel()).thenReturn(internal);

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(RawQuery.builder().query("test query").build())
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).get();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }

    // Because we run tests on this class with Enclosed runner, we need to wrap other tests into class
    public static class OtherTests {

        @Test
        public void completeBuilderShouldThrowExceptionIfNoQueryWasSet() {
            PreparedGetListOfObjects.CompleteBuilder completeBuilder = new PreparedGetListOfObjects.Builder<Object>(mock(StorIOSQLite.class), Object.class)
                    .withQuery(Query.builder().table("test_table").build()); // We will null it later;

            completeBuilder.query = null;

            try {
                completeBuilder.prepare();
                failBecauseExceptionWasNotThrown(IllegalStateException.class);
            } catch (IllegalStateException expected) {
                assertThat(expected).hasMessage("Please specify Query or RawQuery");
            }
        }

        @Test
        public void executeAsBlockingShouldThrowExceptionIfNoQueryWasSet() {
            //noinspection unchecked,ConstantConditions
            PreparedGetListOfObjects<Object> preparedGetListOfObjects
                    = new PreparedGetListOfObjects<Object>(
                    mock(StorIOSQLite.class),
                    Object.class,
                    (Query) null,
                    (GetResolver<Object>) mock(GetResolver.class)
            );

            try {
                preparedGetListOfObjects.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                IllegalStateException cause = (IllegalStateException) expected.getCause();
                assertThat(cause).hasMessage("Please specify query");
            }
        }

        @Test
        public void asRxObservableShouldThrowExceptionIfNoQueryWasSet() {
            //noinspection unchecked,ConstantConditions
            PreparedGetListOfObjects<Object> preparedGetListOfObjects
                    = new PreparedGetListOfObjects<Object>(
                    mock(StorIOSQLite.class),
                    Object.class,
                    (Query) null,
                    (GetResolver<Object>) mock(GetResolver.class)
            );

            try {
                //noinspection ResourceType
                preparedGetListOfObjects.asRxObservable();
                failBecauseExceptionWasNotThrown(IllegalStateException.class);
            } catch (IllegalStateException expected) {
                assertThat(expected).hasMessage("Please specify query");
            }
        }

        @Test
        public void cursorMustBeClosedInCaseOfExceptionForExecuteAsBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final Cursor cursor = mock(Cursor.class);

            when(cursor.getCount()).thenReturn(10);

            when(cursor.moveToNext()).thenReturn(true);

            when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                    .thenReturn(cursor);

            when(getResolver.mapFromCursor(cursor))
                    .thenThrow(new IllegalStateException("test exception"));

            PreparedGetListOfObjects<Object> preparedGetListOfObjects =
                    new PreparedGetListOfObjects<Object>(
                            storIOSQLite,
                            Object.class,
                            Query.builder().table("test_table").build(),
                            getResolver
                    );

            try {
                preparedGetListOfObjects.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException exception) {
                IllegalStateException cause = (IllegalStateException) exception.getCause();
                assertThat(cause).hasMessage("test exception");

                // Cursor must be closed in case of exception
                verify(cursor).close();

                verify(getResolver).performGet(eq(storIOSQLite), any(Query.class));
                verify(getResolver).mapFromCursor(cursor);
                verify(cursor).getCount();
                verify(cursor).moveToNext();

                verifyNoMoreInteractions(storIOSQLite, getResolver, cursor);
            }
        }

        @Test
        public void cursorMustBeClosedInCaseOfExceptionForObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

            when(storIOSQLite.observeChanges()).thenReturn(Observable.<Changes>empty());

            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final Cursor cursor = mock(Cursor.class);

            when(cursor.getCount()).thenReturn(10);

            when(cursor.moveToNext()).thenReturn(true);

            when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                    .thenReturn(cursor);

            when(getResolver.mapFromCursor(cursor))
                    .thenThrow(new IllegalStateException("test exception"));

            PreparedGetListOfObjects<Object> preparedGetListOfObjects =
                    new PreparedGetListOfObjects<Object>(
                            storIOSQLite,
                            Object.class,
                            Query.builder().table("test_table").observesTags("test_tag").build(),
                            getResolver
                    );

            final TestSubscriber<List<Object>> testSubscriber = new TestSubscriber<List<Object>>();

            preparedGetListOfObjects
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();

            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            StorIOException storIOException = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) storIOException.getCause();
            assertThat(cause).hasMessage("test exception");

            // Cursor must be closed in case of exception
            verify(cursor).close();

            verify(storIOSQLite).observeChanges();

            verify(getResolver).performGet(eq(storIOSQLite), any(Query.class));
            verify(getResolver).mapFromCursor(cursor);
            verify(cursor).getCount();
            verify(cursor).moveToNext();
            verify(storIOSQLite).defaultScheduler();

            verifyNoMoreInteractions(storIOSQLite, getResolver, cursor);
        }

        @Test
        public void cursorMustBeClosedInCaseOfExceptionForSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final Cursor cursor = mock(Cursor.class);

            when(cursor.getCount()).thenReturn(10);

            when(cursor.moveToNext()).thenReturn(true);

            when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                    .thenReturn(cursor);

            when(getResolver.mapFromCursor(cursor))
                    .thenThrow(new IllegalStateException("test exception"));

            PreparedGetListOfObjects<Object> preparedGetListOfObjects =
                    new PreparedGetListOfObjects<Object>(
                            storIOSQLite,
                            Object.class,
                            Query.builder().table("test_table").build(),
                            getResolver
                    );

            final TestSubscriber<List<Object>> testSubscriber = new TestSubscriber<List<Object>>();

            preparedGetListOfObjects
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();

            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            StorIOException storIOException = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) storIOException.getCause();
            assertThat(cause).hasMessage("test exception");

            // Cursor must be closed in case of exception
            verify(cursor).close();

            //noinspection unchecked
            verify(getResolver).performGet(eq(storIOSQLite), any(Query.class));
            verify(getResolver).mapFromCursor(cursor);
            verify(cursor).getCount();
            verify(cursor).moveToNext();
            verify(storIOSQLite).defaultScheduler();

            verifyNoMoreInteractions(storIOSQLite, getResolver, cursor);
        }

        @Test
        public void getListOfObjectsObservableExecutesOnSpecifiedScheduler() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOSQLite);

            final PreparedGetListOfObjects<TestItem> operation = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare();

            schedulerChecker.checkAsObservable(operation);
        }

        @Test
        public void getListOfObjectsSingleExecutesOnSpecifiedScheduler() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOSQLite);

            final PreparedGetListOfObjects<TestItem> operation = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void createObservableReturnsAsRxObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            PreparedGetListOfObjects<TestItem> preparedOperation = spy(getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare());

            Observable<List<TestItem>> observable = Observable.just(Collections.<TestItem>emptyList());

            //noinspection CheckResult
            doReturn(observable).when(preparedOperation).asRxObservable();

            //noinspection deprecation
            assertThat(preparedOperation.createObservable()).isEqualTo(observable);

            //noinspection CheckResult
            verify(preparedOperation).asRxObservable();
        }
    }
}
