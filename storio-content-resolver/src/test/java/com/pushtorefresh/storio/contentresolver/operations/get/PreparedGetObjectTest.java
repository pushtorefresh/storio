package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedGetObjectTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldGetObjectWithoutTypeMappingBlocking() {
            final GetObjectStub getStub = GetObjectStub.newStubWithoutTypeMapping();

            final TestItem testItem = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyBehavior(testItem);
        }

        @Test
        public void shouldGetObjectWithoutTypeMappingAsObservable() {
            final GetObjectStub getStub = GetObjectStub.newStubWithoutTypeMapping();

            final Observable<TestItem> testItemObservable = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .asRxObservable()
                    .take(1);

            getStub.verifyBehavior(testItemObservable);
        }

        @Test
        public void shouldGetObjectWithoutTypeMappingAsSingle() {
            final GetObjectStub getStub = GetObjectStub.newStubWithoutTypeMapping();

            final Single<TestItem> testItemSingle = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .asRxSingle();

            getStub.verifyBehavior(testItemSingle);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldGetObjectWithTypeMappingBlocking() {
            final GetObjectStub getStub = GetObjectStub.newStubWithTypeMapping();

            final TestItem testItem = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyBehavior(testItem);
        }

        @Test
        public void shouldGetObjectWithTypeMappingAsObservable() {
            final GetObjectStub getStub = GetObjectStub.newStubWithTypeMapping();

            final Observable<TestItem> testItemObservable = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .asRxObservable()
                    .take(1);

            getStub.verifyBehavior(testItemObservable);
        }

        @Test
        public void shouldGetObjectWithTypeMappingAsSingle() {
            final GetObjectStub getStub = GetObjectStub.newStubWithTypeMapping();

            final Single<TestItem> testItemSingle = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .asRxSingle();

            getStub.verifyBehavior(testItemSingle);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            final PreparedGet<TestItem> preparedGet = storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(Query.builder().uri(mock(Uri.class)).build())
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
                assertThat(expected.getCause()).hasMessage("This type does not have type mapping: " +
                        "type = " + TestItem.class + "," +
                        "ContentProvider was not touched by this operation, please add type mapping for this type");
            }

            verify(storIOContentResolver).get();
            verify(storIOContentResolver).lowLevel();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).query(any(Query.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderAsObservable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            when(storIOContentResolver.observeChangesOfUri(any(Uri.class)))
                    .thenReturn(Observable.<Changes>empty());

            final TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();

            storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(Query.builder().uri(mock(Uri.class)).build())
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).get();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).query(any(Query.class));
            verify(storIOContentResolver).observeChangesOfUri(any(Uri.class));

            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderAsSingle() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            final TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();

            storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(Query.builder().uri(mock(Uri.class)).build())
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).get();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).defaultScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).query(any(Query.class));

            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }
    }

    // With Enclosed runner we can not have tests in root class
    public static class OtherTests {

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

            when(getResolver.mapFromCursor(cursor))
                    .thenThrow(new IllegalStateException("Breaking execution"));

            when(cursor.getCount()).thenReturn(1);

            when(cursor.moveToFirst()).thenReturn(true);

            try {
                new PreparedGetObject.Builder<Object>(storIOContentResolver, Object.class)
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
                verify(cursor).moveToFirst();

                verifyNoMoreInteractions(storIOContentResolver, cursor);
            }
        }


        @Test
        public void getObjectObservableExecutesOnSpecifiedScheduler() {
            final GetObjectStub getStub = GetObjectStub.newStubWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

            final PreparedGetObject<TestItem> operation = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare();

            schedulerChecker.checkAsObservable(operation);
        }

        @Test
        public void getObjectSingleExecutesOnSpecifiedScheduler() {
            final GetObjectStub getStub = GetObjectStub.newStubWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

            final PreparedGetObject<TestItem> operation = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }
    }
}
