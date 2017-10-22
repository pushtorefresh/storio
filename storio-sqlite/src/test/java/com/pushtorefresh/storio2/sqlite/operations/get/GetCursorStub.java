package com.pushtorefresh.storio2.sqlite.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;
import com.pushtorefresh.storio2.test.FlowableBehaviorChecker;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import static io.reactivex.BackpressureStrategy.LATEST;
import static io.reactivex.BackpressureStrategy.MISSING;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

        when(storIOSQLite.observeChanges(any(BackpressureStrategy.class))).thenReturn(Flowable.<Changes>empty());

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
        verify(storIOSQLite).interceptors();
        verify(getResolverForCursor).performGet(storIOSQLite, query);
        assertThat(actualCursor).isSameAs(cursor);
        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyQueryBehaviorForCursor(@NonNull Flowable<Cursor> flowable) {
        new FlowableBehaviorChecker<Cursor>()
                .flowable(flowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Cursor>() {
                    @Override
                    public void accept(Cursor cursor) {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOSQLite).observeChanges(MISSING);
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyQueryBehaviorForCursor(@NonNull Single<Cursor> single) {
        new FlowableBehaviorChecker<Cursor>()
                .flowable(single.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Cursor>() {
                    @Override
                    public void accept(Cursor cursor) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyRawQueryBehaviorForCursor(@NonNull Cursor actualCursor) {
        assertThat(actualCursor).isNotNull();
        verify(storIOSQLite, times(1)).get();
        verify(storIOSQLite).interceptors();
        verify(getResolverForCursor, times(1)).performGet(storIOSQLite, rawQuery);
        assertThat(actualCursor).isSameAs(cursor);
        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyRawQueryBehaviorForCursor(@NonNull Flowable<Cursor> flowable) {
        new FlowableBehaviorChecker<Cursor>()
                .flowable(flowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Cursor>() {
                    @Override
                    public void accept(Cursor cursor) {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOSQLite).observeChanges(LATEST);
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyRawQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfFlowable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }

    void verifyRawQueryBehaviorForCursor(@NonNull Single<Cursor> single) {
        new FlowableBehaviorChecker<Cursor>()
                .flowable(single.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Cursor>() {
                    @Override
                    public void accept(Cursor cursor) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyRawQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfFlowable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }
}
