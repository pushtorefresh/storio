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
        public void shouldGetObjectWithoutTypeMappingAsFlowable() {
            final GetObjectStub getStub = GetObjectStub.newStubWithoutTypeMapping();

            final Flowable<TestItem> testItemFlowable = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING)
                    .take(1);

            getStub.verifyBehavior(testItemFlowable);
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
        public void shouldGetObjectWithTypeMappingAsFlowable() {
            final GetObjectStub getStub = GetObjectStub.newStubWithTypeMapping();

            final Flowable<TestItem> testItemFlowable = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING)
                    .take(1);

            getStub.verifyBehavior(testItemFlowable);
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
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingContentProviderAsFlowable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.get()).thenReturn(new PreparedGet.Builder(storIOContentResolver));

            when(storIOContentResolver.observeChangesOfUri(any(Uri.class), eq(BackpressureStrategy.MISSING)))
                    .thenReturn(Flowable.<Changes>empty());

            final TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();

            storIOContentResolver
                    .get()
                    .object(TestItem.class)
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

            final TestObserver<TestItem> testObserver = new TestObserver<TestItem>();

            storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(Query.builder().uri(mock(Uri.class)).build())
                    .prepare()
                    .asRxSingle()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

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
        public void shouldReturnQueryInGetData() {
            final Query query = Query.builder()
                    .uri(mock(Uri.class))
                    .build();

            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final PreparedGetObject<Object> operation =
                    new PreparedGetObject.Builder<Object>(storIOContentResolver, Object.class)
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
        public void getObjectFlowableExecutesOnSpecifiedScheduler() {
            final GetObjectStub getStub = GetObjectStub.newStubWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

            final PreparedGetObject<TestItem> operation = getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare();

            schedulerChecker.checkAsFlowable(operation);
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

        @Test
        public void shouldPassStorIOContentResolverToGetResolver() {
            final GetObjectStub getStub = GetObjectStub.newStubWithoutTypeMapping();
            getStub.storIOContentResolver
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            verify(getStub.getResolver).mapFromCursor(eq(getStub.storIOContentResolver), any(Cursor.class));
        }
    }
}
