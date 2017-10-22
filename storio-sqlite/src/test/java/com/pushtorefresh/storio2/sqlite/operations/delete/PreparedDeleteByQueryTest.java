package com.pushtorefresh.storio2.sqlite.operations.delete;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;

import org.junit.Test;

import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.MISSING;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PreparedDeleteByQueryTest {

    private class DeleteByQueryStub {
        final StorIOSQLite storIOSQLite;
        final DeleteQuery deleteQuery;
        final DeleteResolver<DeleteQuery> deleteResolver;
        final StorIOSQLite.LowLevel lowLevel;
        final DeleteResult expectedDeleteResult;

        private DeleteByQueryStub() {
            storIOSQLite = mock(StorIOSQLite.class);
            lowLevel = mock(StorIOSQLite.LowLevel.class);

            when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

            deleteQuery = DeleteQuery.builder()
                    .table("test_table")
                    .where("column1 = ?")
                    .whereArgs(1)
                    .affectsTags("test_tag")
                    .build();

            //noinspection unchecked
            deleteResolver = mock(DeleteResolver.class);

            expectedDeleteResult = DeleteResult.newInstance(1, deleteQuery.table(), deleteQuery.affectsTags());

            when(deleteResolver.performDelete(same(storIOSQLite), same(deleteQuery)))
                    .thenReturn(expectedDeleteResult);
        }

        void verifyBehaviour() {
            verify(storIOSQLite).lowLevel();
            verify(storIOSQLite).interceptors();
            verify(deleteResolver).performDelete(same(storIOSQLite), same(deleteQuery));
            verify(lowLevel).notifyAboutChanges(Changes.newInstance(deleteQuery.table(), deleteQuery.affectsTags()));
            verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
        }
    }

    @Test
    public void shouldReturnQueryInGetData() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();
        final PreparedDeleteByQuery prepared = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare();
        assertThat(prepared.getData()).isEqualTo(stub.deleteQuery);
    }

    @Test
    public void shouldPerformDeletionByQueryBlocking() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();

        final DeleteResult actualDeleteResult = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare()
                .executeAsBlocking();

        assertThat(actualDeleteResult).isEqualTo(stub.expectedDeleteResult);
        stub.verifyBehaviour();
    }

    @Test
    public void shouldPerformDeletionByQueryFlowable() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare()
                .asRxFlowable(MISSING)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(stub.expectedDeleteResult);

        verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldPerformDeletionByQuerySingle() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();

        final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();

        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare()
                .asRxSingle()
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValue(stub.expectedDeleteResult);

        verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldPerformDeletionByQueryCompletable() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();

        final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();

        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare()
                .asRxCompletable()
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertNoValues();

        verify(stub.storIOSQLite).defaultRxScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionBlocking() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        //noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = mock(DeleteResolver.class);

        when(deleteResolver.performDelete(same(storIOSQLite), any(DeleteQuery.class)))
                .thenThrow(new IllegalStateException("test exception"));

        try {
            new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build())
                    .withDeleteResolver(deleteResolver)
                    .prepare()
                    .executeAsBlocking();

            failBecauseExceptionWasNotThrown(StorIOException.class);
        } catch (StorIOException expected) {
            IllegalStateException cause = (IllegalStateException) expected.getCause();
            assertThat(cause).hasMessage("test exception");

            verify(deleteResolver).performDelete(same(storIOSQLite), any(DeleteQuery.class));
            verify(storIOSQLite).interceptors();
            verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
        }
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionFlowable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        //noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = mock(DeleteResolver.class);

        when(deleteResolver.performDelete(same(storIOSQLite), any(DeleteQuery.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build())
                .withDeleteResolver(deleteResolver)
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

        verify(deleteResolver).performDelete(same(storIOSQLite), any(DeleteQuery.class));
        verify(storIOSQLite).defaultRxScheduler();
        verify(storIOSQLite).interceptors();
        verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        //noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = mock(DeleteResolver.class);

        when(deleteResolver.performDelete(same(storIOSQLite), any(DeleteQuery.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build())
                .withDeleteResolver(deleteResolver)
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

        verify(deleteResolver).performDelete(same(storIOSQLite), any(DeleteQuery.class));
        verify(storIOSQLite).defaultRxScheduler();
        verify(storIOSQLite).interceptors();
        verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        //noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = mock(DeleteResolver.class);

        when(deleteResolver.performDelete(same(storIOSQLite), any(DeleteQuery.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestObserver<DeleteResult> testObserver = new TestObserver<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build())
                .withDeleteResolver(deleteResolver)
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

        verify(storIOSQLite).defaultRxScheduler();
        verify(deleteResolver).performDelete(same(storIOSQLite), any(DeleteQuery.class));
        verify(storIOSQLite).interceptors();
        verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
    }

    @Test
    public void shouldNotNotifyIfWasNotDeleted() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        final DeleteQuery deleteQuery = DeleteQuery.builder()
                .table("test_table")
                .where("column1 = ?")
                .whereArgs(1)
                .build();

        //noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = mock(DeleteResolver.class);

        final DeleteResult expectedDeleteResult = DeleteResult.newInstance(0, deleteQuery.table()); // No items were deleted

        when(deleteResolver.performDelete(same(storIOSQLite), same(deleteQuery)))
                .thenReturn(expectedDeleteResult);

        final DeleteResult actualDeleteResult = new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery)
                .withDeleteResolver(deleteResolver)
                .prepare()
                .executeAsBlocking();

        assertThat(actualDeleteResult).isEqualTo(expectedDeleteResult);

        verify(deleteResolver).performDelete(same(storIOSQLite), same(deleteQuery));
        verify(lowLevel, never()).notifyAboutChanges(any(Changes.class));
        verify(storIOSQLite).interceptors();
        verifyNoMoreInteractions(storIOSQLite, lowLevel, deleteResolver);
    }

    @Test
    public void deleteByQueryFlowableExecutesOnSpecifiedScheduler() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);

        final PreparedDeleteByQuery operation = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare();

        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void deleteByQuerySingleExecutesOnSpecifiedScheduler() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);

        final PreparedDeleteByQuery operation = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }

    @Test
    public void deleteByQueryCompletableExecutesOnSpecifiedScheduler() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);

        final PreparedDeleteByQuery operation = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare();

        schedulerChecker.checkAsCompletable(operation);
    }
}
