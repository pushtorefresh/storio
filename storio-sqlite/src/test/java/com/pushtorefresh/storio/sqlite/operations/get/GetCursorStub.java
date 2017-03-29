package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import rx.Observable;
import rx.Single;
import rx.functions.Action1;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetCursorStub {

    @NonNull
    final StorIOSQLite storIOSQLite;

    @NonNull
    private final StorIOSQLite.LowLevel lowLevel;

    @NonNull
    final Query query;

    @NonNull
    final RawQuery rawQuery;

    @NonNull
    final GetResolver<Cursor> getResolverForCursor;

    @NonNull
    private final Cursor cursor;

    @SuppressWarnings("unchecked")
    private GetCursorStub() {
        storIOSQLite = mock(StorIOSQLite.class);
        lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel())
                .thenReturn(lowLevel);

        String table = "test_table";
        String tag = "test_tag";

        query = Query
                .builder()
                .table(table)
                .observesTags(tag)
                .build();

        rawQuery = RawQuery
                .builder()
                .query("select * from who_cares")
                .observesTables(table)
                .observesTags(tag)
                .build();

        getResolverForCursor = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        when(storIOSQLite.get())
                .thenReturn(new PreparedGet.Builder(storIOSQLite));

        when(storIOSQLite.observeChanges()).thenReturn(Observable.<Changes>empty());

        assertThat(rawQuery.observesTables()).isNotNull();

        when(getResolverForCursor.performGet(storIOSQLite, query))
                .thenReturn(cursor);

        when(getResolverForCursor.performGet(storIOSQLite, rawQuery))
                .thenReturn(cursor);
    }

    @NonNull
    static GetCursorStub newInstance() {
        return new GetCursorStub();
    }

    void verifyQueryBehaviorForCursor(@NonNull Cursor actualCursor) {
        assertThat(actualCursor).isNotNull();
        verify(storIOSQLite).get();
        verify(getResolverForCursor).performGet(storIOSQLite, query);
        assertThat(actualCursor).isSameAs(cursor);
        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyQueryBehaviorForCursor(@NonNull Observable<Cursor> observable) {
        new ObservableBehaviorChecker<Cursor>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOSQLite).observeChanges();
                        verify(storIOSQLite).defaultScheduler();
                        verifyQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyQueryBehaviorForCursor(@NonNull Single<Cursor> single) {
        new ObservableBehaviorChecker<Cursor>()
                .observable(single.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyRawQueryBehaviorForCursor(@NonNull Cursor actualCursor) {
        assertThat(actualCursor).isNotNull();
        verify(storIOSQLite, times(1)).get();
        verify(getResolverForCursor, times(1)).performGet(storIOSQLite, rawQuery);
        assertThat(actualCursor).isSameAs(cursor);
        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyRawQueryBehaviorForCursor(@NonNull Observable<Cursor> observable) {
        new ObservableBehaviorChecker<Cursor>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOSQLite).observeChanges();
                        verify(storIOSQLite).defaultScheduler();
                        verifyRawQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfObservable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }

    void verifyRawQueryBehaviorForCursor(@NonNull Single<Cursor> single) {
        new ObservableBehaviorChecker<Cursor>()
                .observable(single.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyRawQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfObservable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }
}
