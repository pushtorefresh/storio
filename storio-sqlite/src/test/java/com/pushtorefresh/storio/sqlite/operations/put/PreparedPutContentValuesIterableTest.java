package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import org.junit.Test;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PreparedPutContentValuesIterableTest {

    @Test
    public void putMultipleBlockingWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(true)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putMultipleObservableWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        final Observable<PutResults<ContentValues>> putResultsObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(true)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }

    @Test
    public void putMultipleBlockingWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(false)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putMultipleObservableWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);

        final Observable<PutResults<ContentValues>> putResultsObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(false)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredBlocking() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal()).thenReturn(internal);

        //noinspection unchecked
        final PutResolver<ContentValues> putResolver = mock(PutResolver.class);

        final List<ContentValues> contentValues = singletonList(mock(ContentValues.class));

        when(putResolver.performPut(same(storIOSQLite), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("test exception"));

        try {
            new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues)
                    .withPutResolver(putResolver)
                    .useTransaction(true)
                    .prepare()
                    .executeAsBlocking();

            fail();
        } catch (StorIOException expected) {
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertEquals("test exception", cause.getMessage());

            verify(internal).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal).endTransaction();


            verify(storIOSQLite).internal();
            verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
        }
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredObservable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal()).thenReturn(internal);

        //noinspection unchecked
        final PutResolver<ContentValues> putResolver = mock(PutResolver.class);

        final List<ContentValues> contentValues = singletonList(mock(ContentValues.class));

        when(putResolver.performPut(same(storIOSQLite), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<PutResults<ContentValues>> testSubscriber = new TestSubscriber<PutResults<ContentValues>>();

        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues)
                .withPutResolver(putResolver)
                .useTransaction(true)
                .prepare()
                .createObservable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertEquals("test exception", cause.getMessage());

        verify(internal).beginTransaction();
        verify(internal, never()).setTransactionSuccessful();
        verify(internal).endTransaction();

        verify(storIOSQLite).internal();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionBlocking() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        //noinspection unchecked
        final PutResolver<ContentValues> putResolver = mock(PutResolver.class);

        final List<ContentValues> contentValues = singletonList(mock(ContentValues.class));

        when(putResolver.performPut(same(storIOSQLite), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("test exception"));

        try {
            new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues)
                    .withPutResolver(putResolver)
                    .useTransaction(false)
                    .prepare()
                    .executeAsBlocking();

            fail();
        } catch (StorIOException expected) {
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertEquals("test exception", cause.getMessage());

            // Main check of this test
            verify(internal, never()).endTransaction();

            verify(storIOSQLite).internal();
            verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
        }
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionObservable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        //noinspection unchecked
        final PutResolver<ContentValues> putResolver = mock(PutResolver.class);

        final List<ContentValues> contentValues = singletonList(mock(ContentValues.class));

        when(putResolver.performPut(same(storIOSQLite), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<PutResults<ContentValues>> testSubscriber = new TestSubscriber<PutResults<ContentValues>>();

        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues)
                .withPutResolver(putResolver)
                .useTransaction(false)
                .prepare()
                .createObservable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertEquals("test exception", cause.getMessage());

        // Main check of this test
        verify(internal, never()).endTransaction();

        verify(storIOSQLite).internal();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
    }
}
