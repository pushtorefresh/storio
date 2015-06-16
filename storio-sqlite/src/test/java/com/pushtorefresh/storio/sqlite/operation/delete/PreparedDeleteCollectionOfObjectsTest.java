package com.pushtorefresh.storio.sqlite.operation.delete;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
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
                    .objects(deleteStub.items)
                    .useTransaction(false)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehavior(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithTransactionBlocking() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.items)
                    .useTransaction(true)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehavior(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithoutTransactionAsObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithoutTransaction();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.items)
                    .useTransaction(false)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehavior(observable);
        }

        @Test
        public void shouldDeleteObjectsWithoutTypeMappingWithTransactionAsObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithoutTypeMappingWithTransaction();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.items)
                    .useTransaction(true)
                    .withDeleteResolver(deleteStub.deleteResolver)
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehavior(observable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithoutTransactionBlocking() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.items)
                    .useTransaction(false)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehavior(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithTransactionBlocking() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithTransaction();

            final DeleteResults<TestItem> deleteResults = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.items)
                    .useTransaction(true)
                    .prepare()
                    .executeAsBlocking();

            deleteStub.verifyBehavior(deleteResults);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithoutTransactionObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithoutTransaction();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.items)
                    .useTransaction(false)
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehavior(observable);
        }

        @Test
        public void shouldDeleteObjectsWithTypeMappingWithTransactionObservable() {
            final DeleteStub deleteStub
                    = DeleteStub.newStubForMultipleObjectsWithTypeMappingWithTransaction();

            final Observable<DeleteResults<TestItem>> observable = deleteStub.storIOSQLite
                    .delete()
                    .objects(deleteStub.items)
                    .useTransaction(true)
                    .prepare()
                    .createObservable();

            deleteStub.verifyBehavior(observable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedDelete<DeleteResults<TestItem>> preparedDelete = storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(false)
                    .prepare();

            try {
                preparedDelete.executeAsBlocking();
                fail();
            } catch (IllegalStateException expected) {
                // it's okay, no type mapping was found
            }

            verify(storIOSQLite).delete();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(false)
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedDelete<DeleteResults<TestItem>> preparedDelete = storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(true)
                    .prepare();

            try {
                preparedDelete.executeAsBlocking();
                fail();
            } catch (IllegalStateException expected) {
                // it's okay, no type mapping was found
            }

            verify(storIOSQLite).delete();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.delete()).thenReturn(new PreparedDelete.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<DeleteResults<TestItem>> testSubscriber = new TestSubscriber<DeleteResults<TestItem>>();

            storIOSQLite
                    .delete()
                    .objects(items)
                    .useTransaction(true)
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(IllegalStateException.class);

            verify(storIOSQLite).delete();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).delete(any(DeleteQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }
}
