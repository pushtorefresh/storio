package com.pushtorefresh.storio.sqlite.operations.delete;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

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
public class PreparedDeleteCollectionOfObjectsTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithoutTransactionBlocking() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForMultipleObjects(deleteResults);
        }

        @Test
        public void shouldNotNotifyIfWasNotDeletedObjectsWithoutTypeMappingWithoutTransaction() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransactionNothingDeleted();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForMultipleObjects(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithTransactionBlocking() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForMultipleObjects(deleteResults);
        }

        @Test
        public void shouldNotNotifyIfWasNotDeletedObjectsWithoutTypeMappingWithTransaction() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithTransactionNothingDeleted();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForMultipleObjects(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithoutTransactionAsObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxObservable();

            deleteStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithTransactionAsObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxObservable();

            deleteStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithoutTransactionAsSingle() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final Single<DeleteResults<TestItem>> single = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxSingle();

            deleteStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithTransactionAsSingle() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final Single<DeleteResults<TestItem>> single = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxSingle();

            deleteStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithoutTransactionAsCompletable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final Completable completable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxCompletable();

            deleteStub.verifyBehaviorForMultipleObjects(completable);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithTransactionAsCompletable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final Completable completable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxCompletable();

            deleteStub.verifyBehaviorForMultipleObjects(completable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithoutTransactionBlocking() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForMultipleObjects(deleteResults);
        }

        @Test
        public void shouldNotNotifyIfWasNotDeletedObjectsWithTypeMappingWithoutTransaction() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransactionNothingDeleted();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForMultipleObjects(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithTransactionBlocking() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithTransaction();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForMultipleObjects(deleteResults);
        }

        @Test
        public void shouldNotNotifyIfWasNotDeletedObjectsWithTypeMappingWithTransaction() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithTransactionNothingDeleted();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehaviorForMultipleObjects(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithoutTransactionObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .prepare()
                    .asRxObservable();

            deleteStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithTransactionObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithTransaction();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .prepare()
                    .asRxObservable();

            deleteStub.verifyBehaviorForMultipleObjects(observable);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithoutTransactionSingle() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final Single<DeleteResults<TestItem>> single = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .prepare()
                    .asRxSingle();

            deleteStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithTransactionSingle() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithTransaction();

            final Single<DeleteResults<TestItem>> single = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .prepare()
                    .asRxSingle();

            deleteStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithoutTransactionCompletable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final Completable completable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .prepare()
                    .asRxCompletable();

            deleteStub.verifyBehaviorForMultipleObjects(completable);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithTransactionCompletable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithTransaction();

            final Completable completable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .prepare()
                    .asRxCompletable();

            deleteStub.verifyBehaviorForMultipleObjects(completable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedDelete<DeleteResults<TestItem>> preparedDelete = storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(false)
                    .prepare();

            try {
                preparedDelete.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOSQLite).delete();
            verify(storIOSQLite).lowLevel();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
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

            verify(storIOSQLite).delete();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
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

            verify(storIOSQLite).delete();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
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

            verify(storIOSQLite).delete();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedDelete<DeleteResults<TestItem>> preparedDelete = storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(true)
                    .prepare();

            try {
                preparedDelete.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOSQLite).delete();
            verify(storIOSQLite).lowLevel();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
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

            verify(storIOSQLite).delete();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
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

            verify(storIOSQLite).delete();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
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

            verify(storIOSQLite).delete();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }

    public static class OtherTests {

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            //noinspection unchecked
            final DeleteResolver<Object> deleteResolver = mock(DeleteResolver.class);

            when(deleteResolver.performDelete(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            try {
                new PreparedDeleteCollectionOfObjects.Builder<Object>(storIOSQLite, singletonList(new Object()))
                        .useTransaction(true)
                        .withDeleteResolver(deleteResolver)
                        .prepare()
                        .executeAsBlocking();

                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                IllegalStateException cause = (IllegalStateException) expected.getCause();
                assertThat(cause).hasMessage("test exception");

                verify(internal).beginTransaction();
                verify(internal, never()).setTransactionSuccessful();
                verify(internal).endTransaction();

                verify(storIOSQLite).lowLevel();
                verify(deleteResolver).performDelete(same(storIOSQLite), any());
                verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
            }
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            //noinspection unchecked
            final DeleteResolver<Object> deleteResolver = mock(DeleteResolver.class);

            when(deleteResolver.performDelete(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final TestSubscriber<DeleteResults<Object>> testSubscriber = new TestSubscriber<DeleteResults<Object>>();

            new PreparedDeleteCollectionOfObjects.Builder<Object>(storIOSQLite, singletonList(new Object()))
                    .useTransaction(true)
                    .withDeleteResolver(deleteResolver)
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

            verify(internal).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(deleteResolver).performDelete(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            //noinspection unchecked
            final DeleteResolver<Object> deleteResolver = mock(DeleteResolver.class);

            when(deleteResolver.performDelete(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final TestSubscriber<DeleteResults<Object>> testSubscriber = new TestSubscriber<DeleteResults<Object>>();

            new PreparedDeleteCollectionOfObjects.Builder<Object>(storIOSQLite, singletonList(new Object()))
                    .useTransaction(true)
                    .withDeleteResolver(deleteResolver)
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

            verify(internal).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(deleteResolver).performDelete(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

            //noinspection unchecked
            final DeleteResolver<Object> deleteResolver = mock(DeleteResolver.class);

            when(deleteResolver.performDelete(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final TestSubscriber<DeleteResults<Object>> testSubscriber = new TestSubscriber<DeleteResults<Object>>();

            new PreparedDeleteCollectionOfObjects.Builder<Object>(storIOSQLite, singletonList(new Object()))
                    .useTransaction(true)
                    .withDeleteResolver(deleteResolver)
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

            verify(internal).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultScheduler();
            verify(deleteResolver).performDelete(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
        }

        @Test
        public void deleteCollectionOfObjectsObservableExecutesOnSpecifiedScheduler() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOSQLite);

            final PreparedDeleteCollectionOfObjects<TestItem> operation = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare();

            schedulerChecker.checkAsObservable(operation);
        }

        @Test
        public void deleteCollectionOfObjectsSingleExecutesOnSpecifiedScheduler() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOSQLite);

            final PreparedDeleteCollectionOfObjects<TestItem> operation = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void deleteCollectionOfObjectsCompletableExecutesOnSpecifiedScheduler() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOSQLite);

            final PreparedDeleteCollectionOfObjects<TestItem> operation = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare();

            schedulerChecker.checkAsCompletable(operation);
        }

        @Test
        public void createObservableReturnsAsRxObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            PreparedDeleteCollectionOfObjects<TestItem> preparedOperation = spy(deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .prepare());

            Observable<DeleteResults<Object>> observable =
                    Observable.just(DeleteResults.newInstance(Collections.<Object, DeleteResult>emptyMap()));

            //noinspection CheckResult
            doReturn(observable).when(preparedOperation).asRxObservable();

            //noinspection deprecation
            assertThat(preparedOperation.createObservable()).isEqualTo(observable);

            //noinspection CheckResult
            verify(preparedOperation).asRxObservable();
        }
    }
}
