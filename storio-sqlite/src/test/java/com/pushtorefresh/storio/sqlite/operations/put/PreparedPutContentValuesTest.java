package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio.StorIOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class PreparedPutContentValuesTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void putContentValuesBlocking() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();

        final PutResult putResult = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForOneContentValues(putResult);
    }

    @Test
    public void putContentValuesObservable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();

        final Observable<PutResult> putResultObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .asRxObservable();

        putStub.verifyBehaviorForOneContentValues(putResultObservable);
    }

    @Test
    public void putContentValuesSingle() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();

        final Single<PutResult> putResultSingle = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .asRxSingle();

        putStub.verifyBehaviorForOneContentValues(putResultSingle);
    }

    @Test
    public void putContentValuesCompletable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();

        final Completable completable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .asRxCompletable();

        putStub.verifyBehaviorForOneContentValues(completable);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionBlocking() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();

        ContentValues contentValues = stub.contentValues.get(0);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(stub.storIOSQLite, contentValues);

        expectedException.expect(StorIOException.class);
        expectedException.expectMessage(startsWith("Error has occurred during Put operation. contentValues ="));
        expectedException.expectCause(equalTo(testException));

        stub.storIOSQLite
                .put()
                .contentValues(contentValues)
                .withPutResolver(stub.putResolver)
                .prepare()
                .executeAsBlocking();

        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionObservable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();

        ContentValues contentValues = stub.contentValues.get(0);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(stub.storIOSQLite, contentValues);

        final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();

        stub.storIOSQLite
                .put()
                .contentValues(contentValues)
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
        verify(stub.storIOSQLite).defaultScheduler();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();

        ContentValues contentValues = stub.contentValues.get(0);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(stub.storIOSQLite, contentValues);

        final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();

        stub.storIOSQLite
                .put()
                .contentValues(contentValues)
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
        verify(stub.storIOSQLite).defaultScheduler();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForOneContentValues();

        ContentValues contentValues = stub.contentValues.get(0);

        IllegalStateException testException = new IllegalStateException("test exception");
        doThrow(testException).when(stub.putResolver).performPut(stub.storIOSQLite, contentValues);

        final TestSubscriber testSubscriber = new TestSubscriber();

        stub.storIOSQLite
                .put()
                .contentValues(contentValues)
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
        verify(stub.storIOSQLite).defaultScheduler();
        verifyNoMoreInteractions(stub.storIOSQLite, stub.lowLevel);
    }

    @Test
    public void createObservableReturnsAsRxObservable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();

        PreparedPutContentValues preparedOperation = spy(putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare());

        Observable<PutResult> observable = Observable.just(PutResult.newInsertResult(1, TestItem.TABLE));

        //noinspection CheckResult
        doReturn(observable).when(preparedOperation).asRxObservable();

        //noinspection deprecation
        assertThat(preparedOperation.createObservable()).isEqualTo(observable);

        //noinspection CheckResult
        verify(preparedOperation).asRxObservable();
    }
}
