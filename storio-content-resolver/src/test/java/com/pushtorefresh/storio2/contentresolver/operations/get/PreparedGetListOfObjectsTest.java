package com.pushtorefresh.storio2.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.contentresolver.Changes;
import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio2.contentresolver.queries.Query;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
        public void shouldGetListOfObjectsWithoutTypeMappingAsFlowable() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithoutTypeMapping();

            final Flowable<List<TestItem>> testItemsFlowable = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING)
                    .take(1);

            getStub.verifyBehavior(testItemsFlowable);
        }

        @Test
        public void shouldGetListOfObjectsWithoutTypeMappingAsSingle() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithoutTypeMapping();

            final Single<List<TestItem>> testItemsSingle = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .asRxSingle();

            getStub.verifyBehavior(testItemsSingle);
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
        public void shouldGetListOfObjectsWithTypeMappingAsFlowable() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithTypeMapping();

            final Flowable<List<TestItem>> testItemsFlowable = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING)
                    .take(1);

            getStub.verifyBehavior(testItemsFlowable);
        }

        @Test
        public void shouldGetListOfObjectsWithTypeMappingAsSingle() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithTypeMapping();

            final Single<List<TestItem>> testItemsSingle = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .asRxSingle();

            getStub.verifyBehavior(testItemsSingle);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            final PreparedGet<List<TestItem>> preparedGet = storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(Query.builder().uri(mock(Uri.class)).build())
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOContentResolver).get();
            verify(storIOContentResolver).lowLevel();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).query(any(Query.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderAsFlowable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            when(storIOContentResolver.observeChangesOfUri(any(Uri.class), eq(BackpressureStrategy.MISSING)))
                    .thenReturn(Flowable.<Changes>empty());

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(Query.builder().uri(mock(Uri.class)).build())
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING)
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).get();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).query(any(Query.class));
            verify(storIOContentResolver).observeChangesOfUri(any(Uri.class), eq(BackpressureStrategy.MISSING));

            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderAsSingle() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            final TestObserver<List<TestItem>> testObserver = new TestObserver<List<TestItem>>();

            storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(Query.builder().uri(mock(Uri.class)).build())
                    .prepare()
                    .asRxSingle()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            Throwable error = testObserver.errors().get(0);

            assertThat(error)
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasMessageStartingWith("Error has occurred during Get operation. query = Query{uri=Mock for Uri, hashCode: ")
                    .hasMessageEndingWith(", columns=[], where='', whereArgs=[], sortOrder=''}");

            assertThat(error.getCause())
                    .hasMessage("This type does not have type mapping: type = class com.pushtorefresh.storio2.contentresolver.operations.get.TestItem,ContentProvider was not touched by this operation, please add type mapping for this type");

            verify(storIOContentResolver).get();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).query(any(Query.class));

            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }
    }

    // With Enclosed runner we can not have tests in root class
    public static class OtherTests {

        @Test
        public void shouldReturnItemsInGetData() {
            final Query query = Query.builder()
                    .uri(mock(Uri.class))
                    .build();

            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final PreparedGetListOfObjects<Object> operation =
                    new PreparedGetListOfObjects.Builder<Object>(storIOContentResolver, Object.class)
                    .withQuery(query)
                    .withGetResolver(getResolver)
                    .prepare();

            assertThat(operation.getData()).isEqualTo(query);
        }

        @Test
        public void shouldCloseCursorInCaseOfException() {
            StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);

            Query query = Query.builder()
                    .uri(mock(Uri.class))
                    .build();

            //noinspection unchecked
            GetResolver<Object> getResolver = mock(GetResolver.class);

            Cursor cursor = mock(Cursor.class);

            when(getResolver.performGet(storIOContentResolver, query))
                    .thenReturn(cursor);

            when(getResolver.mapFromCursor(storIOContentResolver, cursor))
                    .thenThrow(new IllegalStateException("Breaking execution"));

            when(cursor.getCount()).thenReturn(1);

            when(cursor.moveToNext()).thenReturn(true);

            try {
                new PreparedGetListOfObjects.Builder<Object>(storIOContentResolver, Object.class)
                        .withQuery(query)
                        .withGetResolver(getResolver)
                        .prepare()
                        .executeAsBlocking();

                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                assertThat(expected.getCause())
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Breaking execution");

                // Main check: in case of exception cursor must be closed
                verify(cursor).close();

                verify(cursor).getCount();
                verify(cursor).moveToNext();

                verifyNoMoreInteractions(storIOContentResolver, cursor);
            }
        }

        @Test
        public void getListOfObjectsFlowableExecutesOnSpecifiedScheduler() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

            final PreparedGetListOfObjects<TestItem> operation = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare();

            schedulerChecker.checkAsFlowable(operation);
        }

        @Test
        public void getListOfObjectsSingleExecutesOnSpecifiedScheduler() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

            final PreparedGetListOfObjects<TestItem> operation = getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void shouldPassStorIOContentResolverToGetResolver() {
            final GetObjectsStub getStub = GetObjectsStub.newStubWithoutTypeMapping();
            getStub.storIOContentResolver
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            verify(getStub.getResolver, times(getStub.items.size()))
                    .mapFromCursor(eq(getStub.storIOContentResolver), any(Cursor.class));
        }
    }
}
