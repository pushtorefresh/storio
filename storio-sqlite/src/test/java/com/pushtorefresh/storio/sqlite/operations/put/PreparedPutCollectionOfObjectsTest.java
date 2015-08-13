package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
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
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
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
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

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
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
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
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOSQLite).put();
            verify(storIOSQLite).internal();
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
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), anyObject()))
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

                verify(internal).beginTransaction();
                verify(internal, never()).setTransactionSuccessful();
                verify(internal).endTransaction();

                verify(storIOSQLite).internal();
                verify(putResolver).performPut(same(storIOSQLite), anyObject());
                verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
            }
        }

        @Test
        public void shouldFinishTransactionIfExceptionHasOccurredObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), anyObject()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            final TestSubscriber<PutResults<Object>> testSubscriber = new TestSubscriber<PutResults<Object>>();

            new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                    .useTransaction(true)
                    .withPutResolver(putResolver)
                    .prepare()
                    .createObservable()
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

            verify(storIOSQLite).internal();
            verify(putResolver).performPut(same(storIOSQLite), anyObject());
            verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
        }

        @Test
        public void verifyBehaviorInCaseOfExceptionWithoutTransactionBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), anyObject()))
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
                verify(internal, never()).beginTransaction();
                verify(internal, never()).setTransactionSuccessful();
                verify(internal, never()).endTransaction();

                verify(storIOSQLite).internal();
                verify(putResolver).performPut(same(storIOSQLite), anyObject());
                verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
            }
        }

        @Test
        public void verifyBehaviorInCaseOfExceptionWithoutTransactionObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.internal()).thenReturn(internal);

            //noinspection unchecked
            final PutResolver<Object> putResolver = mock(PutResolver.class);

            when(putResolver.performPut(same(storIOSQLite), anyObject()))
                    .thenThrow(new IllegalStateException("test exception"));

            final List<Object> objects = singletonList(new Object());

            final TestSubscriber<PutResults<Object>> testSubscriber = new TestSubscriber<PutResults<Object>>();

            new PreparedPutCollectionOfObjects.Builder<Object>(storIOSQLite, objects)
                    .useTransaction(false)
                    .withPutResolver(putResolver)
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            //noinspection ThrowableResultOfMethodCallIgnored
            StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            // Main checks of this test
            verify(internal, never()).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal, never()).endTransaction();

            verify(storIOSQLite).internal();
            verify(putResolver).performPut(same(storIOSQLite), anyObject());
            verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
        }
    }
}
