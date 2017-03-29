package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedPutCollectionOfObjectsTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldPutObjectsWithoutTypeMappingWithoutTransactionBlocking() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingWithTransactionBlocking() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingWithoutTransactionAsObservable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final Observable<PutResults<TestItem>> observable = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingWithTransactionAsObservable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final Observable<PutResults<TestItem>> observable = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingWithoutTransactionAsSingle() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final Single<PutResults<TestItem>> single = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingWithTransactionAsSingle() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final Single<PutResults<TestItem>> single = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingWithoutTransactionAsCompletable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final Completable completable = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForMultipleObjects(completable);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingWithTransactionAsCompletable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final Completable completable = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForMultipleObjects(completable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldPutObjectsWithTypeMappingWithoutTransactionBlocking() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingWithTransactionBlocking() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithTransaction();

            final PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingWithoutTransactionAsObservable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final Observable<PutResults<TestItem>> observable = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingWithTransactionAsObservable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithTransaction();

            final Observable<PutResults<TestItem>> observable = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .prepare()
                    .asRxObservable();

            putStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingWithoutTransactionAsSingle() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final Single<PutResults<TestItem>> single = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingWithTransactionAsSingle() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithTransaction();

            final Single<PutResults<TestItem>> single = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingWithoutTransactionAsCompletable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final Completable completable = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForMultipleObjects(completable);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingWithTransactionAsCompletable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithTransaction();

            final Completable completable = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForMultipleObjects(completable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPut<PutResults<TestItem>> preparedPut = storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(false)
                    .prepare();

            try {
                preparedPut.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOSQLite).put();
            verify(storIOSQLite).lowLevel();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(false)
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(false)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(false)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPut<PutResults<TestItem>> preparedPut = storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(true)
                    .prepare();

            try {
                preparedPut.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOSQLite).put();
            verify(storIOSQLite).lowLevel();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(true)
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(true)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(true)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }

    public static class OtherTests {

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            try {
                new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                        .useTransaction(true)
                        .withPutResolver(putResolver)
                        .prepare()
                        .executeAsBlocking();

                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                IllegalStateException cause = (IllegalStateException) expected.getCause();
                assertThat(cause).hasMessage("test exception");

                verify(lowLevel).beginTransaction();
                verify(lowLevel, never()).setTransactionSuccessful();
                verify(lowLevel).endTransaction();

                verify(storIOSQLite).lowLevel();
                verify(putResolver).performPut(same(storIOSQLite), any());
                verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
            }
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            final TestSubscriber<PutResults<Object>> testSubscriber = new TestSubscriber<PutResults<Object>>();

            new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                    .useTransaction(true)
                    .withPutResolver(putResolver)
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            verify(lowLevel).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(putResolver).performPut(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            final TestSubscriber<PutResults<Object>> testSubscriber = new TestSubscriber<PutResults<Object>>();

            new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                    .useTransaction(true)
                    .withPutResolver(putResolver)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            verify(lowLevel).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(putResolver).performPut(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            final TestSubscriber<PutResults<Object>> testSubscriber = new TestSubscriber<PutResults<Object>>();

            new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                    .useTransaction(true)
                    .withPutResolver(putResolver)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            verify(lowLevel).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(putResolver).performPut(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }

        @Test
        public void verifyBehaviorInCaseOfExceptionWithoutTransactionBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            try {
                new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                        .useTransaction(false)
                        .withPutResolver(putResolver)
                        .prepare()
                        .executeAsBlocking();

                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                IllegalStateException cause = (IllegalStateException) expected.getCause();
                assertThat(cause).hasMessage("test exception");

                // Main checks of this test
                verify(lowLevel, never()).beginTransaction();
                verify(lowLevel, never()).setTransactionSuccessful();
                verify(lowLevel, never()).endTransaction();

                verify(storIOSQLite).lowLevel();
                verify(putResolver).performPut(same(storIOSQLite), any());
                verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
            }
        }

        @Test
        public void verifyBehaviorInCaseOfExceptionWithoutTransactionObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            final TestSubscriber<PutResults<Object>> testSubscriber = new TestSubscriber<PutResults<Object>>();

            new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                    .useTransaction(false)
                    .withPutResolver(putResolver)
                    .prepare()
                    .asRxObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            // Main checks of this test
            verify(lowLevel, never()).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel, never()).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(putResolver).performPut(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }

        @Test
        public void verifyBehaviorInCaseOfExceptionWithoutTransactionSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            final TestSubscriber<PutResults<Object>> testSubscriber = new TestSubscriber<PutResults<Object>>();

            new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                    .useTransaction(false)
                    .withPutResolver(putResolver)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            // Main checks of this test
            verify(lowLevel, never()).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel, never()).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(putResolver).performPut(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }

        @Test
        public void verifyBehaviorInCaseOfExceptionWithoutTransactionCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            final TestSubscriber<PutResults<Object>> testSubscriber = new TestSubscriber<PutResults<Object>>();

            new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                    .useTransaction(false)
                    .withPutResolver(putResolver)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            // Main checks of this test
            verify(lowLevel, never()).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel, never()).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(putResolver).performPut(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }

        @Test
        public void putCollectionOfObjectsObservableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithTransaction();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .prepare();

            schedulerChecker.checkAsObservable(operation);
        }

        @Test
        public void putCollectionOfObjectsSingleExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithTransaction();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void putCollectionOfObjectsCompletableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithTransaction();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .prepare();

            schedulerChecker.checkAsCompletable(operation);
        }

        @Test
        public void createObservableReturnsAsRxObservable() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMappingWithTransaction();

            PreparedPutCollectionOfObjects<TestItem> preparedOperation = spy(putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .prepare());

            Observable<PutResults<TestItem>> observable =
                    Observable.just(PutResults.newInstance(Collections.<TestItem, PutResult>emptyMap()));

            //noinspection CheckResult
            doReturn(observable).when(preparedOperation).asRxObservable();

            //noinspection deprecation
            assertThat(preparedOperation.createObservable()).isEqualTo(observable);

            //noinspection CheckResult
            verify(preparedOperation).asRxObservable();
        }
    }
}
