package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;

import org.junit.Test;

import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
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
                .asRxObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }

    @Test
    public void putMultipleSingleWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        final Single<PutResults<ContentValues>> putResultsSingle = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(true)
                .prepare()
                .asRxSingle();

        putStub.verifyBehaviorForMultipleContentValues(putResultsSingle);
    }

    @Test
    public void putMultipleCompletableWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        final Completable completable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(true)
                .prepare()
                .asRxCompletable();

        putStub.verifyBehaviorForMultipleContentValues(completable);
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
                .asRxObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }

    @Test
    public void putMultipleSingleWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);

        final Single<PutResults<ContentValues>> putResultsSingle = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(false)
                .prepare()
                .asRxSingle();

        putStub.verifyBehaviorForMultipleContentValues(putResultsSingle);
    }

    @Test
    public void putMultipleCompletableWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);

        final Completable completable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(false)
                .prepare()
                .asRxCompletable();

        putStub.verifyBehaviorForMultipleContentValues(completable);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredBlocking() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

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

            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            verify(internal).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal).endTransaction();


            verify(storIOSQLite).lowLevel();
            verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
        }
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredObservable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

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
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredSingle() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

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
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredCompletable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

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

            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            // Main check of this test
            verify(internal, never()).endTransaction();

            verify(storIOSQLite).lowLevel();
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
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        // Main check of this test
        verify(internal, never()).endTransaction();

        verify(storIOSQLite).lowLevel();
        verify(storIOSQLite).defaultScheduler();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionSingle() {
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
                .asRxSingle()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        // Main check of this test
        verify(internal, never()).endTransaction();

        verify(storIOSQLite).lowLevel();
        verify(storIOSQLite).defaultScheduler();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionCompletable() {
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
                .asRxCompletable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        // Main check of this test
        verify(internal, never()).endTransaction();

        verify(storIOSQLite).lowLevel();
        verify(storIOSQLite).defaultScheduler();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, internal, putResolver);
    }

    @Test
    public void putMultipleObservableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsObservable(operation);
    }

    @Test
    public void putMultipleSingleExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void putMultipleCompletableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsCompletable(operation);
    }
}
