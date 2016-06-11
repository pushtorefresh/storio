package com.pushtorefresh.storio.contentresolver.operations.delete;

import com.pushtorefresh.storio.contentresolver.operations.SchedulerChecker;

import org.junit.Test;

import rx.Completable;
import rx.Observable;
import rx.Single;

public class PreparedDeleteByQueryTest {

    @Test
    public void shouldDeleteByQueryBlocking() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();

        final DeleteResult deleteResult = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .executeAsBlocking();

        deleteStub.verifyBehavior(deleteResult);
    }

    @Test
    public void shouldDeleteByQueryAsObservable() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();

        final Observable<DeleteResult> deleteResultObservable = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .asRxObservable();

        deleteStub.verifyBehavior(deleteResultObservable);
    }

    @Test
    public void shouldDeleteByQueryAsSingle() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();

        final Single<DeleteResult> deleteResultSingle = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .asRxSingle();

        deleteStub.verifyBehavior(deleteResultSingle);
    }

    @Test
    public void shouldDeleteByQueryAsCompletable() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();

        final Completable completable = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .asRxCompletable();

        deleteStub.verifyBehavior(completable);
    }

    @Test
    public void deleteByQueryObservableExecutesOnSpecifiedScheduler() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOContentResolver);

        final PreparedDeleteByQuery operation = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare();

        schedulerChecker.checkAsObservable(operation);
    }

    @Test
    public void deleteByQuerySingleExecutesOnSpecifiedScheduler() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOContentResolver);

        final PreparedDeleteByQuery operation = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void deleteByQueryCompletableExecutesOnSpecifiedScheduler() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOContentResolver);

        final PreparedDeleteByQuery operation = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare();

        schedulerChecker.checkAsCompletable(operation);
    }
}
