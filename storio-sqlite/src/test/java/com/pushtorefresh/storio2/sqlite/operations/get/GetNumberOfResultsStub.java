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
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GetNumberOfResultsStub {

    @NonNull
    final StorIOSQLite storIOSQLite;

    @NonNull
    private final StorIOSQLite.LowLevel lowLevel;

    @NonNull
    final Query query;

    @NonNull
    final RawQuery rawQuery;

    @NonNull
    final GetResolver<Integer> getResolverForNumberOfResults;

    @NonNull
    private final Cursor cursor;

    @NonNull
    private final Integer numberOfResults;

    private GetNumberOfResultsStub() {
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

        //noinspection unchecked
        getResolverForNumberOfResults = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        numberOfResults = 129;

        when(storIOSQLite.get())
                .thenReturn(new PreparedGet.Builder(storIOSQLite));

        when(storIOSQLite.observeChanges(any(BackpressureStrategy.class))).thenReturn(Flowable.<Changes>empty());

        assertThat(rawQuery.observesTables()).isNotNull();

        when(getResolverForNumberOfResults.performGet(storIOSQLite, query))
                .thenReturn(cursor);

        when(getResolverForNumberOfResults.performGet(storIOSQLite, rawQuery))
                .thenReturn(cursor);

        when(getResolverForNumberOfResults.mapFromCursor(storIOSQLite, cursor))
                .thenReturn(numberOfResults);
    }

    @NonNull
    static GetNumberOfResultsStub newInstance() {
        return new GetNumberOfResultsStub();
    }

    void verifyQueryBehaviorForInteger(@NonNull Integer actualNumberOfResults) {
        assertThat(actualNumberOfResults).isNotNull();
        verify(storIOSQLite).get();
        verify(storIOSQLite).interceptors();
        verify(getResolverForNumberOfResults).performGet(storIOSQLite, query);
        assertThat(actualNumberOfResults).isSameAs(numberOfResults);
        verify(cursor).close();
        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyQueryBehaviorForInteger(@NonNull Flowable<Integer> flowable) {
        new FlowableBehaviorChecker<Integer>()
                .flowable(flowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer numberOfResults) {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOSQLite).observeChanges(LATEST);
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyQueryBehaviorForInteger(@NonNull Single<Integer> single) {
        new FlowableBehaviorChecker<Integer>()
                .flowable(single.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer numberOfResults) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyRawQueryBehaviorForInteger(@NonNull Integer actualNumberOfResults) {
        assertThat(actualNumberOfResults).isNotNull();
        verify(storIOSQLite).get();
        verify(storIOSQLite).interceptors();
        verify(getResolverForNumberOfResults).performGet(storIOSQLite, rawQuery);
        assertThat(actualNumberOfResults).isSameAs(numberOfResults);
        verify(cursor).close();
        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyRawQueryBehaviorForInteger(@NonNull Flowable<Integer> flowable) {
        new FlowableBehaviorChecker<Integer>()
                .flowable(flowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer numberOfResults) {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOSQLite).observeChanges(LATEST);
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyRawQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfFlowable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }

    void verifyRawQueryBehaviorForInteger(@NonNull Single<Integer> single) {
        new FlowableBehaviorChecker<Integer>()
                .flowable(single.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer numberOfResults) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyRawQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfFlowable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }
}
