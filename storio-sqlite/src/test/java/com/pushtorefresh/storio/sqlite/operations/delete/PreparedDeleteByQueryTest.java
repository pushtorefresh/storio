package com.pushtorefresh.storio.sqlite.operations.delete;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import org.junit.Test;

import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PreparedDeleteByQueryTest {

    @Test
    public void shouldPerformDeletionByQueryBlocking() {
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

        final DeleteResult expectedDeleteResult = DeleteResult.newInstance(1, deleteQuery.table());

        when(deleteResolver.performDelete(same(storIOSQLite), same(deleteQuery)))
                .thenReturn(expectedDeleteResult);

        final DeleteResult actualDeleteResult = new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery)
                .withDeleteResolver(deleteResolver)
                .prepare()
                .executeAsBlocking();

        assertThat(actualDeleteResult).isEqualTo(expectedDeleteResult);

        verify(storIOSQLite).lowLevel();
        verify(deleteResolver).performDelete(same(storIOSQLite), same(deleteQuery));
        verify(internal).notifyAboutChanges(eq(Changes.newInstance(deleteQuery.table())));
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
    }

    @Test
    public void shouldPerformDeletionByQueryObservable() {
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

        final DeleteResult expectedDeleteResult = DeleteResult.newInstance(1, deleteQuery.table());

        when(deleteResolver.performDelete(same(storIOSQLite), same(deleteQuery)))
                .thenReturn(expectedDeleteResult);

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery)
                .withDeleteResolver(deleteResolver)
                .prepare()
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(expectedDeleteResult);

        verify(storIOSQLite).lowLevel();
        verify(deleteResolver).performDelete(same(storIOSQLite), same(deleteQuery));
        verify(internal).notifyAboutChanges(eq(Changes.newInstance(deleteQuery.table())));
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
    }

    @Test
    public void shouldPerformDeletionByQuerySingle() {
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

        final DeleteResult expectedDeleteResult = DeleteResult.newInstance(1, deleteQuery.table());

        when(deleteResolver.performDelete(same(storIOSQLite), same(deleteQuery)))
                .thenReturn(expectedDeleteResult);

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery)
                .withDeleteResolver(deleteResolver)
                .prepare()
                .asRxSingle()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(expectedDeleteResult);

        verify(storIOSQLite).lowLevel();
        verify(deleteResolver).performDelete(same(storIOSQLite), same(deleteQuery));
        verify(internal).notifyAboutChanges(eq(Changes.newInstance(deleteQuery.table())));
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
    }

    @Test
    public void shouldPerformDeletionByQueryCompletable() {
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

        final DeleteResult expectedDeleteResult = DeleteResult.newInstance(1, deleteQuery.table());

        when(deleteResolver.performDelete(same(storIOSQLite), same(deleteQuery)))
                .thenReturn(expectedDeleteResult);

        final TestSubscriber<DeleteResult> testSubscriber = new TestSubscriber<DeleteResult>();

        new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery)
                .withDeleteResolver(deleteResolver)
                .prepare()
                .asRxComletable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();

        verify(storIOSQLite).lowLevel();
        verify(deleteResolver).performDelete(same(storIOSQLite), same(deleteQuery));
        verify(internal).notifyAboutChanges(eq(Changes.newInstance(deleteQuery.table())));
        verifyNoMoreInteractions(storIOSQLite, internal, deleteResolver);
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
                .asRxComletable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(StorIOException.class);

        //noinspection ThrowableResultOfMethodCallIgnored
        StorIOException expected = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

        IllegalStateException cause = (IllegalStateException) expected.getCause();
        assertThat(cause).hasMessage("test exception");

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
}
