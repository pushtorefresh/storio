package com.pushtorefresh.storio2.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.Changes;
import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.queries.Query;
import com.pushtorefresh.storio2.test.FlowableBehaviorChecker;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetCursorStub {

    @NonNull
    final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final StorIOContentResolver.LowLevel lowLevel;

    @NonNull
    final Query query;

    @NonNull
    final GetResolver<Cursor> getResolver;

    @NonNull
    final Cursor cursor;

    @SuppressWarnings("unchecked")
    private GetCursorStub() {
        storIOContentResolver = mock(StorIOContentResolver.class);
        lowLevel = mock(StorIOContentResolver.LowLevel.class);

        query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        getResolver = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        when(storIOContentResolver.lowLevel())
                .thenReturn(lowLevel);

        when(storIOContentResolver.get())
                .thenReturn(new PreparedGet.Builder(storIOContentResolver));

        when(getResolver.performGet(storIOContentResolver, query))
                .thenReturn(cursor);

        when(storIOContentResolver.observeChangesOfUri(query.uri(), BackpressureStrategy.MISSING))
                .thenReturn(Flowable.<Changes>empty());

        when(getResolver.mapFromCursor(storIOContentResolver, cursor))
                .thenReturn(cursor);
    }

    @NonNull
    static GetCursorStub newInstance() {
        return new GetCursorStub();
    }

    void verifyQueryBehaviorForCursor(@NonNull Cursor actualCursor) {
        verify(storIOContentResolver, times(1)).get();
        verify(getResolver, times(1)).performGet(storIOContentResolver, query);
        assertThat(actualCursor).isSameAs(cursor);
        verify(cursor, times(0)).close();
        verifyNoMoreInteractions(storIOContentResolver, lowLevel, getResolver, cursor);
    }

    void verifyQueryBehaviorForCursor(@NonNull Flowable<Cursor> observable) {
        new FlowableBehaviorChecker<Cursor>()
                .flowable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Cursor>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Cursor cursor) throws Exception {
                        // Get Operation should be subscribed to changes of Uri
                        verify(storIOContentResolver).observeChangesOfUri(query.uri(), BackpressureStrategy.MISSING);

                        verify(storIOContentResolver).defaultRxScheduler();

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
                    public void accept(@io.reactivex.annotations.NonNull Cursor cursor) throws Exception {
                        verify(storIOContentResolver).defaultRxScheduler();
                        verifyQueryBehaviorForCursor(cursor);
                    }
                })
                .checkBehaviorOfFlowable();
    }
}
