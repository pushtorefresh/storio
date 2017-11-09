package com.pushtorefresh.storio2.sqlite.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.SchedulerChecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.MISSING;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PreparedPutContentValuesIterableTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnContentValuesInGetData() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);

        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        assertThat(operation.getData()).isEqualTo(putStub.contentValues);
    }

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
    public void putMultipleFlowableWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        final Flowable<PutResults<ContentValues>> putResultsFlowable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(true)
                .prepare()
                .asRxFlowable(MISSING);

        putStub.verifyBehaviorForMultipleContentValues(putResultsFlowable);
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
    public void putMultipleFlowableWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);

        final Flowable<PutResults<ContentValues>> putResultsFlowable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(false)
                .prepare()
                .asRxFlowable(MISSING);

        putStub.verifyBehaviorForMultipleContentValues(putResultsFlowable);
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
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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

            verify(lowLevel).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel).endTransaction();


            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).interceptors();
            verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredFlowable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

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
        verify(storIOSQLite).defaultRxScheduler();
        verify(storIOSQLite).interceptors();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredSingle() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        //noinspection unchecked
        final PutResolver<ContentValues> putResolver = mock(PutResolver.class);

        final List<ContentValues> contentValues = singletonList(mock(ContentValues.class));

        when(putResolver.performPut(same(storIOSQLite), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestObserver<PutResults<ContentValues>> testObserver = new TestObserver<PutResults<ContentValues>>();

        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues)
                .withPutResolver(putResolver)
                .useTransaction(true)
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
        verify(storIOSQLite).defaultRxScheduler();
        verify(storIOSQLite).interceptors();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void shouldFinishTransactionIfExceptionHasOccurredCompletable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        //noinspection unchecked
        final PutResolver<ContentValues> putResolver = mock(PutResolver.class);

        final List<ContentValues> contentValues = singletonList(mock(ContentValues.class));

        when(putResolver.performPut(same(storIOSQLite), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestObserver<PutResults<ContentValues>> testObserver = new TestObserver<PutResults<ContentValues>>();

        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues)
                .withPutResolver(putResolver)
                .useTransaction(true)
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
        verify(storIOSQLite).defaultRxScheduler();
        verify(storIOSQLite).interceptors();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionBlocking() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

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
            verify(lowLevel, never()).endTransaction();

            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).interceptors();
            verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
            verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
        }
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionFlowable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

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
                .asRxFlowable(MISSING)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.errors().get(0);

        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        // Main check of this test
        verify(lowLevel, never()).endTransaction();

        verify(storIOSQLite).lowLevel();
        verify(storIOSQLite).defaultRxScheduler();
        verify(storIOSQLite).interceptors();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionSingle() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        //noinspection unchecked
        final PutResolver<ContentValues> putResolver = mock(PutResolver.class);

        final List<ContentValues> contentValues = singletonList(mock(ContentValues.class));

        when(putResolver.performPut(same(storIOSQLite), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestObserver<PutResults<ContentValues>> testObserver = new TestObserver<PutResults<ContentValues>>();

        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues)
                .withPutResolver(putResolver)
                .useTransaction(false)
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

        // Main check of this test
        verify(lowLevel, never()).endTransaction();

        verify(storIOSQLite).lowLevel();
        verify(storIOSQLite).defaultRxScheduler();
        verify(storIOSQLite).interceptors();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void verifyBehaviorInCaseOfExceptionWithoutTransactionCompletable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        //noinspection unchecked
        final PutResolver<ContentValues> putResolver = mock(PutResolver.class);

        final List<ContentValues> contentValues = singletonList(mock(ContentValues.class));

        when(putResolver.performPut(same(storIOSQLite), any(ContentValues.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestObserver<PutResults<ContentValues>> testObserver = new TestObserver<PutResults<ContentValues>>();

        new PreparedPutContentValuesIterable.Builder(storIOSQLite, contentValues)
                .withPutResolver(putResolver)
                .useTransaction(false)
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

        // Main check of this test
        verify(lowLevel, never()).endTransaction();

        verify(storIOSQLite).lowLevel();
        verify(storIOSQLite).defaultRxScheduler();
        verify(storIOSQLite).interceptors();
        verify(putResolver).performPut(same(storIOSQLite), any(ContentValues.class));
        verifyNoMoreInteractions(storIOSQLite, lowLevel, putResolver);
    }

    @Test
    public void putMultipleFlowableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOSQLite);

        final PreparedPutContentValuesIterable operation = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsFlowable(operation);
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
    public void shouldWrapExceptionIntoStorIOExceptionFlowable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(eq(stub.storIOSQLite), any(ContentValues.class));

        final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();

        stub.storIOSQLite
                .put()
                .contentValues(stub.contentValues)
                .withPutResolver(stub.putResolver)
                .prepare()
                .asRxFlowable(MISSING)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.errors().get(0);

        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        verify(stub.storIOSQLite).put();
        verify(stub.storIOSQLite).lowLevel();
        verify(stub.lowLevel).beginTransaction();
        verify(stub.lowLevel).endTransaction();
        verify(stub.storIOSQLite).defaultRxScheduler();
        verify(stub.storIOSQLite).interceptors();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(eq(stub.storIOSQLite), any(ContentValues.class));

        final TestObserver<Object> testObserver = new TestObserver<Object>();

        stub.storIOSQLite
                .put()
                .contentValues(stub.contentValues)
                .withPutResolver(stub.putResolver)
                .prepare()
                .asRxSingle()
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testObserver.errors().get(0);

        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        verify(stub.storIOSQLite).put();
        verify(stub.storIOSQLite).lowLevel();
        verify(stub.lowLevel).beginTransaction();
        verify(stub.lowLevel).endTransaction();
        verify(stub.storIOSQLite).defaultRxScheduler();
        verify(stub.storIOSQLite).interceptors();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(eq(stub.storIOSQLite), any(ContentValues.class));

        final TestObserver testObserver = new TestObserver();

        stub.storIOSQLite
                .put()
                .contentValues(stub.contentValues)
                .withPutResolver(stub.putResolver)
                .prepare()
                .asRxCompletable()
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testObserver.errors().get(0);

        assertThat(expected).hasMessageStartingWith("Error has occurred during Put operation. contentValues =");
        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

        verify(stub.storIOSQLite).put();
        verify(stub.storIOSQLite).lowLevel();
        verify(stub.lowLevel).beginTransaction();
        verify(stub.lowLevel).endTransaction();
        verify(stub.storIOSQLite).defaultRxScheduler();
        verify(stub.storIOSQLite).interceptors();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldNotNotifyIfCollectionEmptyWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForEmptyCollectionWithoutTransaction();

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .objects(putStub.contentValues)
                .useTransaction(false)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void shouldNotNotifyIfCollectionEmptyWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForEmptyCollectionWithTransaction();

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .objects(putStub.contentValues)
                .useTransaction(true)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithoutTransaction() {
        final PutContentValuesStub putStub
                = PutContentValuesStub.newPutStubForMultipleContentValuesWithoutInsertsAndUpdatesWithoutTransaction();

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .objects(putStub.contentValues)
                .useTransaction(false)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void shouldNotNotifyIfWasNotInsertedAndUpdatedWithTransaction() {
        final PutContentValuesStub putStub
                = PutContentValuesStub.newPutStubForMultipleContentValuesWithoutInsertsAndUpdatesWithTransaction();

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .objects(putStub.contentValues)
                .useTransaction(true)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }
}
