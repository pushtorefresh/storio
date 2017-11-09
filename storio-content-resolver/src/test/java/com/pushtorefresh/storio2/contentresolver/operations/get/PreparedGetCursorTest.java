package com.pushtorefresh.storio2.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio2.contentresolver.queries.Query;

import org.junit.Test;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PreparedGetCursorTest {

    @Test
    public void shouldReturnQueryInGetData() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final PreparedGetCursor operation = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare();

        assertThat(operation.getData()).isEqualTo(getStub.query);
    }

    @Test
    public void getCursorBlocking() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Cursor cursor = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForCursor(cursor);
    }


    @Test
    public void getCursorFlowable() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Flowable<Cursor> cursorFlowable = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .asRxFlowable(BackpressureStrategy.MISSING)
                .take(1);

        getStub.verifyQueryBehaviorForCursor(cursorFlowable);
    }

    @Test
    public void getCursorSingle() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Single<Cursor> cursorFlowable = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .asRxSingle();

        getStub.verifyQueryBehaviorForCursor(cursorFlowable);
    }

    @Test
    public void shouldUseStandardGetResolverWithoutExplicitlyPassed() {
        StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);

        StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

        when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        new PreparedGetCursor.Builder(storIOContentResolver)
                .withQuery(query)
                .prepare()
                .executeAsBlocking();

        verify(storIOContentResolver).lowLevel();
        verify(lowLevel).query(query);

        verifyNoMoreInteractions(storIOContentResolver, lowLevel);
    }

    @Test
    public void checkThatStandardGetResolverDoesNotModifyCursor() {
        StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
        Cursor cursor = mock(Cursor.class);

        Cursor cursorAfterMap = PreparedGetCursor
                .CompleteBuilder
                .STANDARD_GET_RESOLVER
                .mapFromCursor(storIOContentResolver, cursor);

        assertThat(cursorAfterMap).isEqualTo(cursor);
    }

    @Test
    public void getCursorFlowableExecutesOnSpecifiedScheduler() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

        final PreparedGetCursor operation = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare();

        schedulerChecker.checkAsFlowable(operation);
    }

    @Test
    public void getCursorSingleExecutesOnSpecifiedScheduler() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

        final PreparedGetCursor operation = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare();

        schedulerChecker.checkAsSingle(operation);
    }
}
