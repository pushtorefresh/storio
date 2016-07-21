package com.pushtorefresh.storio.contentresolver.operations.put;

import com.pushtorefresh.storio.contentresolver.operations.SchedulerChecker;

import org.junit.Test;

import rx.Completable;
import rx.Observable;
import rx.Single;

public class PreparedPutContentValuesTest {

    @Test
    public void putContentValuesBlocking() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();

        final PutResult putResult = putStub.storIOContentResolver
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

        final Observable<PutResult> putResultObservable = putStub.storIOContentResolver
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

        final Single<PutResult> putResultSingle = putStub.storIOContentResolver
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

        final Completable completable = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .asRxCompletable();

        putStub.verifyBehaviorForOneContentValues(completable);
    }

    @Test
    public void putContentValuesObservableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

        final PreparedPutContentValues operation = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsObservable(operation);
    }

    @Test
    public void putContentValuesSingleExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

        final PreparedPutContentValues operation = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void putContentValuesCompletableExecutesOnSpecifiedScheduler() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

        final PreparedPutContentValues operation = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare();

        schedulerChecker.checkAsCompletable(operation);
    }
}
