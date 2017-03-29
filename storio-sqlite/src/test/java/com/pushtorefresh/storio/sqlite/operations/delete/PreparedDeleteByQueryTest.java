package com.pushtorefresh.storio.sqlite.operations.delete;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.SchedulerChecker;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PreparedDeleteByQueryTest {

    private class DeleteByQueryStub {
        final StorIOSQLite storIOSQLite;
        final DeleteQuery deleteQuery;
        final DeleteResolver<DeleteQuery> deleteResolver;
        final StorIOSQLite.Internal internal;
        final DeleteResult expectedDeleteResult;

        private DeleteByQueryStub() {
            storIOSQLite = mock(StorIOSQLite.class);
            internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.lowLevel()).thenReturn(internal);

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
            verify(deleteResolver).performDelete(same(storIOSQLite), same(deleteQuery));
            verify(internal).notifyAboutChanges(Changes.newInstance(deleteQuery.table(), deleteQuery.affectsTags()));
            verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
        }
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
    public void shouldPerformDeletionByQueryObservable() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare()
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(stub.expectedDeleteResult);

        verify(stub.storIOSQLite).defaultScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldPerformDeletionByQuerySingle() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare()
                .asRxSingle()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(stub.expectedDeleteResult);

        verify(stub.storIOSQLite).defaultScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldPerformDeletionByQueryCompletable() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare()
                .asRxCompletable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();

        verify(stub.storIOSQLite).defaultScheduler();
        stub.verifyBehaviour();
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionBlocking() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

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
            verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
        }
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionObservable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

        //noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = mock(DeleteResolver.class);

        when(deleteResolver.performDelete(same(storIOSQLite), any(DeleteQuery.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build())
                .withDeleteResolver(deleteResolver)
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

        verify(deleteResolver).performDelete(same(storIOSQLite), any(DeleteQuery.class));
        verify(storIOSQLite).defaultScheduler();
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionSingle() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

        //noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = mock(DeleteResolver.class);

        when(deleteResolver.performDelete(same(storIOSQLite), any(DeleteQuery.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build())
                .withDeleteResolver(deleteResolver)
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

        verify(deleteResolver).performDelete(same(storIOSQLite), any(DeleteQuery.class));
        verify(storIOSQLite).defaultScheduler();
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
    }

    @Test
    public void shouldWrapExceptionIntoStorIOExceptionCompletable() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

        //noinspection unchecked
        final DeleteResolver<DeleteQuery> deleteResolver = mock(DeleteResolver.class);

        when(deleteResolver.performDelete(same(storIOSQLite), any(DeleteQuery.class)))
                .thenThrow(new IllegalStateException("test exception"));

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, DeleteQuery.builder().table("test_table").build())
                .withDeleteResolver(deleteResolver)
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

        verify(storIOSQLite).defaultScheduler();
        verify(deleteResolver).performDelete(same(storIOSQLite), any(DeleteQuery.class));
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
    }

    @Test
    public void shouldNotNotifyIfWasNotDeleted() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel()).thenReturn(internal);

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
        verify(internal, never()).notifyAboutChanges(any(Changes.class));
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
    }

    @Test
    public void deleteByQueryObservableExecutesOnSpecifiedScheduler() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(stub.storIOSQLite);

        final PreparedDeleteByQuery operation = new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                .withDeleteResolver(stub.deleteResolver)
                .prepare();

        schedulerChecker.checkAsObservable(operation);
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

    @Test
    public void createObservableReturnsAsRxObservable() {
        final DeleteByQueryStub stub = new DeleteByQueryStub();

        PreparedDeleteByQuery preparedOperation =
                spy(new PreparedDeleteByQuery.Builder(stub.storIOSQLite, stub.deleteQuery)
                        .withDeleteResolver(stub.deleteResolver)
                        .prepare());

        Observable<DeleteResult> observable = Observable.just(DeleteResult.newInstance(1, TestItem.TABLE));
        //noinspection CheckResult
        doReturn(observable).when(preparedOperation).asRxObservable();

        //noinspection deprecation
        assertThat(preparedOperation.createObservable()).isEqualTo(observable);

        //noinspection CheckResult
        verify(preparedOperation).asRxObservable();
    }
}
