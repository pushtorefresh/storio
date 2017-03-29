package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;

import rx.Observable;
import rx.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PreparedGetCursorTest {

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
    public void getCursorObservable() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Observable<Cursor> cursorObservable = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .asRxObservable()
                .take(1);

        getStub.verifyQueryBehaviorForCursor(cursorObservable);
    }

    @Test
    public void getCursorSingle() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Single<Cursor> cursorObservable = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .asRxSingle();

        getStub.verifyQueryBehaviorForCursor(cursorObservable);
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
        Cursor cursor = mock(Cursor.class);

        Cursor cursorAfterMap = PreparedGetCursor
                .CompleteBuilder
                .STANDARD_GET_RESOLVER
                .mapFromCursor(cursor);

        assertThat(cursorAfterMap).isEqualTo(cursor);
    }

    @Test
    public void getCursorObservableExecutesOnSpecifiedScheduler() {
        final GetCursorStub getStub = GetCursorStub.newInstance();
        final SchedulerChecker schedulerChecker = SchedulerChecker.create(getStub.storIOContentResolver);

        final PreparedGetCursor operation = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare();

        schedulerChecker.checkAsObservable(operation);
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
