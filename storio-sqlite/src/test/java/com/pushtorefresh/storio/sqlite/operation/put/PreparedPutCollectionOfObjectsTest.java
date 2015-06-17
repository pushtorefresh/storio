package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;

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
                    .createObservable();

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
                    .createObservable();

            putStub.verifyBehaviorForMultipleObjects(observable);
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
                    .createObservable();

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
                    .createObservable();

            putStub.verifyBehaviorForMultipleObjects(observable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPut<PutResults<TestItem>> preparedPut = storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(false)
                    .prepare();

            try {
                preparedPut.executeAsBlocking();
                fail();
            } catch (IllegalStateException expected) {
                // it's okay, no type mapping was found
            }

            verify(storIOSQLite).put();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(false)
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPut<PutResults<TestItem>> preparedPut = storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(true)
                    .prepare();

            try {
                preparedPut.executeAsBlocking();
                fail();
            } catch (IllegalStateException expected) {
                // it's okay, no type mapping was found
            }

            verify(storIOSQLite).put();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithTransactionWithoutAffectingDbAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            when(storIOSQLite.put()).thenReturn(new PreparedPut.Builder(storIOSQLite));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOSQLite
                    .put()
                    .objects(items)
                    .useTransaction(true)
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(internal, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }
}
