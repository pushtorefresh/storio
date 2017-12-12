package com.pushtorefresh.storio3.contentresolver.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.SchedulerChecker;

import org.junit.Test;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PreparedPutContentValuesIterableTest {

    @Test
    public void shouldReturnContentValuesInGetData() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();

        final PreparedPutContentValuesIterable operation = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        assertThat(operation.getData()).isEqualTo(putStub.contentValues);
    }

    @Test
    public void putContentValuesIterableBlocking() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();

        final PutResults<ContentValues> putResults = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putContentValuesIterableFlowable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();

        final Flowable<PutResults<ContentValues>> putResultsFlowable = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING);

        putStub.verifyBehaviorForMultipleContentValues(putResultsFlowable);
    }

    @Test
    public void putContentValuesIterableSingle() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();

        final Single<PutResults<ContentValues>> putResultsSingle = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .asRxSingle();

        putStub.verifyBehaviorForMultipleContentValues(putResultsSingle);
    }

    @Test
    public void putContentValuesIterableCompletable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();

        final Completable completable = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .asRxCompletable();

        putStub.verifyBehaviorForMultipleContentValues(completable);
    }

    @Test
    public void putContentValuesIterableFlowableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

        final PreparedPutContentValuesIterable operation = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void putContentValuesIterableSingleExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

        final PreparedPutContentValuesIterable operation = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void putContentValuesIterableCompletableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

        final PreparedPutContentValuesIterable operation = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsCompletable(operation);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOException() {
        final PutContentValuesStub stub = PutContentValuesStub.newPutStubForMultipleContentValues();

        Throwable throwable = new IllegalStateException("Test exception");
        when(stub.putResolver.performPut(any(StorIOContentResolver.class), any(ContentValues.class)))
                .thenThrow(throwable);

        final PreparedPutContentValuesIterable operation = stub.storIOContentResolver
                .put()
                .contentValues(stub.contentValues)
                .withPutResolver(stub.putResolver)
                .prepare();

        try {
            operation.executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            assertThat(expected)
                    .hasMessageStartingWith("Error has occurred during Put operation. contentValues = ")
                    .hasCause(throwable);
        }
    }
}
