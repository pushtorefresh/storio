package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PreparedPutContentValuesIterableTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionBlocking() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(eq(stub.storIOSQLite), any(ContentValues.class));

        expectedException.expect(StorIOException.class);
        expectedException.expectMessage(startsWith("Error has occurred during Put operation. contentValues ="));
        expectedException.expectCause(equalTo(testException));

        stub.storIOSQLite
                .put()
                .contentValues(stub.contentValues)
                .withPutResolver(stub.putResolver)
                .prepare()
                .executeAsBlocking();

        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionObservable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(eq(stub.storIOSQLite), any(ContentValues.class));

        final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();

        stub.storIOSQLite
                .put()
                .contentValues(stub.contentValues)
                .withPutResolver(stub.putResolver)
                .prepare()
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        verify(stub.storIOSQLite).put();
        verify(stub.storIOSQLite).lowLevel();
        verify(stub.lowLevel).beginTransaction();
        verify(stub.lowLevel).endTransaction();
        verify(stub.storIOSQLite).defaultScheduler();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(eq(stub.storIOSQLite), any(ContentValues.class));

        final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();

        stub.storIOSQLite
                .put()
                .contentValues(stub.contentValues)
                .withPutResolver(stub.putResolver)
                .prepare()
                .asRxSingle()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        verify(stub.storIOSQLite).put();
        verify(stub.storIOSQLite).lowLevel();
        verify(stub.lowLevel).beginTransaction();
        verify(stub.lowLevel).endTransaction();
        verify(stub.storIOSQLite).defaultScheduler();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(eq(stub.storIOSQLite), any(ContentValues.class));

        final TestSubscriber testSubscriber = new TestSubscriber();

        stub.storIOSQLite
                .put()
                .contentValues(stub.contentValues)
                .withPutResolver(stub.putResolver)
                .prepare()
                .asRxCompletable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        verify(stub.storIOSQLite).put();
        verify(stub.storIOSQLite).lowLevel();
        verify(stub.lowLevel).beginTransaction();
        verify(stub.lowLevel).endTransaction();
        verify(stub.storIOSQLite).defaultScheduler();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void createObservableReturnsAsRxObservable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        PreparedPutContentValuesIterable preparedOperation = spy(putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(true)
                .prepare());

        Observable<PutResults<ContentValues>> observable =
                Observable.just(PutResults.newInstance(Collections.<ContentValues, PutResult>emptyMap()));

        //noinspection CheckResult
        doReturn(observable).when(preparedOperation).asRxObservable();

        //noinspection deprecation
        assertThat(preparedOperation.createObservable()).isEqualTo(observable);

        //noinspection CheckResult
        verify(preparedOperation).asRxObservable();
    }
}
