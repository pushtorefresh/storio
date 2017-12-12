package com.pushtorefresh.storio3.contentresolver.operations.delete;

import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio3.contentresolver.queries.DeleteQuery;

import org.junit.Test;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PreparedDeleteByQueryTest {

    @Test
    public void shouldReturnQueryInGetData() {
        final DeleteByQueryStub stub = DeleteByQueryStub.newInstance();
        final PreparedDeleteByQuery prepared = new PreparedDeleteByQuery.Builder(stub.storIOContentResolver, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare();
        assertThat(prepared.getData()).isEqualTo(stub.deleteQuery);
    }

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
    public void shouldDeleteByQueryAsFlowable() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();

        final Flowable<DeleteResult> deleteResultFlowable = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING);

        deleteStub.verifyBehavior(deleteResultFlowable);
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
    public void deleteByQueryFlowableExecutesOnSpecifiedScheduler() {
        final DeleteByQueryStub deleteStub = DeleteByQueryStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(deleteStub.storIOContentResolver);

        final PreparedDeleteByQuery operation = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.deleteQuery)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare();

        schedulerChecker.checkAsFlowable(operation);
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

    @Test
    public void shouldWrapExceptionIntoStorIOException() {
        final DeleteByQueryStub stub = DeleteByQueryStub.newInstance();

        Throwable throwable = new IllegalStateException("Test exception");
        when(stub.deleteResolver.performDelete(any(StorIOContentResolver.class), any(DeleteQuery.class)))
                .thenThrow(throwable);

        final PreparedDeleteByQuery operation = stub.storIOContentResolver
                .delete()
                .byQuery(stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare();

        try {
            operation.executeAsBlocking();
            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            assertThat(expected)
                    .hasMessageStartingWith("Error has occurred during Delete operation. query = ")
                    .hasCause(throwable);
        }
    }
}
