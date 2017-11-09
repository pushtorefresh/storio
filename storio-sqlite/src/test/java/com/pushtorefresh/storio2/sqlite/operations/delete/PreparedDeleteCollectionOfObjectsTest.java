package com.pushtorefresh.storio2.sqlite.operations.delete;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.MISSING;
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
        public void shouldDeleteObjectsWithoutTypeMappingWithoutTransactionAsFlowable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final Flowable<DeleteResults<TestItem>> flowable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxFlowable(MISSING);

            deleteStub.verifyBehaviorForMultipleObjects(flowable);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithTransactionAsFlowable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final Flowable<DeleteResults<TestItem>> flowable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .asRxFlowable(MISSING);

            deleteStub.verifyBehaviorForMultipleObjects(flowable);
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
        public void shouldDeleteObjectsWithTypeMappingWithoutTransactionFlowable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final Flowable<DeleteResults<TestItem>> flowable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(false)
                    .prepare()
                    .asRxFlowable(MISSING);

            deleteStub.verifyBehaviorForMultipleObjects(flowable);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithTransactionFlowable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithTransaction();

            final Flowable<DeleteResults<TestItem>> flowable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .useTransaction(true)
                    .prepare()
                    .asRxFlowable(MISSING);

            deleteStub.verifyBehaviorForMultipleObjects(flowable);
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
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedDelete<DeleteResults<TestItem>, Collection<TestItem>> preparedDelete = storIOSQLite
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
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).lowLevel();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsFlowable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(false)
                    .prepare()
                    .asRxFlowable(MISSING)
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestObserver<DeleteResults<TestItem>> testObserver = new TestObserver<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(false)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestObserver<DeleteResults<TestItem>> testObserver = new TestObserver<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(false)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedDelete<DeleteResults<TestItem>, Collection<TestItem>> preparedDelete = storIOSQLite
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
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).lowLevel();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsFlowable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(true)
                    .prepare()
                    .asRxFlowable(MISSING)
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestObserver<DeleteResults<TestItem>> testObserver = new TestObserver<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(true)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestObserver<DeleteResults<TestItem>> testObserver = new TestObserver<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(true)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel);
        }
    }

    public static class OtherTests {

        @Test
        public void shouldReturnItemsInGetData() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            //noinspection unchecked
            final DeleteResolver<Object> deleteResolver = mock(DeleteResolver.class);
            final List<Object> items = singletonList(new Object());
            final PreparedDeleteCollectionOfObjects<Object> operation =
                    new PreparedDeleteCollectionOfObjects.Builder<Object>(storIOSQLite, items)
                            .useTransaction(true)
                            .withDeleteResolver(deleteResolver)
                            .prepare();

            assertThat(operation.getData()).isEqualTo(items);
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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

                verify(lowLevel).beginTransaction();
                verify(lowLevel, never()).setTransactionSuccessful();
                verify(lowLevel).endTransaction();

                verify(storIOSQLite).lowLevel();
                verify(storIOSQLite).interceptors();
                verify(deleteResolver).performDelete(same(storIOSQLite), any());
                verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
            }
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredFlowable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final DeleteResolver<Object> deleteResolver = mock(DeleteResolver.class);

            when(deleteResolver.performDelete(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final TestSubscriber<DeleteResults<Object>> testSubscriber = new TestSubscriber<DeleteResults<Object>>();

            new PreparedDeleteCollectionOfObjects.Builder<Object>(storIOSQLite, singletonList(new Object()))
                    .useTransaction(true)
                    .withDeleteResolver(deleteResolver)
                    .prepare()
                    .asRxFlowable(MISSING)
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.errors().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            verify(lowLevel).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).defaultRxScheduler();
            verify(deleteResolver).performDelete(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredSingle() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final DeleteResolver<Object> deleteResolver = mock(DeleteResolver.class);

            when(deleteResolver.performDelete(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final TestObserver<DeleteResults<Object>> testObserver = new TestObserver<DeleteResults<Object>>();

            new PreparedDeleteCollectionOfObjects.Builder<Object>(storIOSQLite, singletonList(new Object()))
                    .useTransaction(true)
                    .withDeleteResolver(deleteResolver)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            testObserver.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testObserver.errors().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            verify(lowLevel).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).defaultRxScheduler();
            verify(deleteResolver).performDelete(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredCompletable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            //noinspection unchecked
            final DeleteResolver<Object> deleteResolver = mock(DeleteResolver.class);

            when(deleteResolver.performDelete(same(storIOSQLite), any()))
                    .thenThrow(new IllegalStateException("test exception"));

            final TestObserver<DeleteResults<Object>> testObserver = new TestObserver<DeleteResults<Object>>();

            new PreparedDeleteCollectionOfObjects.Builder<Object>(storIOSQLite, singletonList(new Object()))
                    .useTransaction(true)
                    .withDeleteResolver(deleteResolver)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            testObserver.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testObserver.errors().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            verify(lowLevel).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).interceptors();
            verify(storIOSQLite).defaultRxScheduler();
            verify(deleteResolver).performDelete(same(storIOSQLite), any());
            verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
        }

        @Test
        public void deleteCollectionOfObjectsFlowableExecutesOnSpecifiedScheduler() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOSQLite);

            final PreparedDeleteCollectionOfObjects<TestItem> operation = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.itemsRequestedForDelete)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare();

            schedulerChecker.checkAsFlowable(operation);
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
    }
}
