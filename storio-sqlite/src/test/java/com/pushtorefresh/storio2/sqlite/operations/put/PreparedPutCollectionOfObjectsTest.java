package com.pushtorefresh.storio2.sqlite.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio2.sqlite.queries.UpdateQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

        @Test
        public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithoutTypeMappingWithoutTransaction() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutInsertsAndUpdatesWithoutTypeMappingWithoutTransaction();

            PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithoutTypeMappingWithTransaction() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutInsertsAndUpdatesWithoutTypeMappingWithTransaction();

            PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
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

        @Test
        public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithTypeMappingWithoutTransaction() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutInsertsAndUpdatesWithTypeMappingWithoutTransaction();

            PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithTypeMappingWithTransaction() {
            final PutObjectsStub putStub
                    = PutObjectsStub.newPutStubForMultipleObjectsWithoutInsertsAndUpdatesWithTypeMappingWithTransaction();

            PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPut<PutResults<TestItem>, Collection<TestItem>> preparedPut = storIOSQLite
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
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPut<PutResults<TestItem>, Collection<TestItem>> preparedPut = storIOSQLite
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
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }
    }

    public static class OtherTests {

        @Test
        public void shouldReturnItemsInGetData() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

            //noinspection unchecked
            final PutResolver<TestItem> putResolver = mock(PutResolver.class);

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPutCollectionOfObjects<TestItem> operation =
                    new PreparedPutCollectionOfObjects.Builder<TestItem>(storIOSQLite, items)
                            .useTransaction(true)
                            .withPutResolver(putResolver)
                            .prepare();

            assertThat(operation.getData()).isEqualTo(items);
        }

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
                verify(storIOSQLite).interceptors();
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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
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
                verify(storIOSQLite).interceptors();
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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
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
            verify(storIOSQLite).defaultRxScheduler();
            verify(storIOSQLite).interceptors();
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
        public void shouldNotNotifyIfCollectionEmptyWithoutTransaction() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForEmptyCollectionWithoutTransaction();

            PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(false)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldNotNotifyIfCollectionEmptyWithTransaction() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForEmptyCollectionWithTransaction();

            PutResults<TestItem> putResults = putStub.storIOSQLite
                    .put()
                    .objects(putStub.items)
                    .useTransaction(true)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }
    }
}
