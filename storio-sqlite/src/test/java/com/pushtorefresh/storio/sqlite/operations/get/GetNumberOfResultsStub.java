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

        when(storIOSQLite.observeChanges()).thenReturn(Observable.<Changes>empty());

        assertThat(rawQuery.observesTables()).isNotNull();

        when(getResolverForNumberOfResults.performGet(storIOSQLite, query))
                .thenReturn(cursor);

        when(getResolverForNumberOfResults.performGet(storIOSQLite, rawQuery))
                .thenReturn(cursor);

        when(getResolverForNumberOfResults.mapFromCursor(cursor))
                .thenReturn(numberOfResults);
    }

    @NonNull
    static GetNumberOfResultsStub newInstance() {
        return new GetNumberOfResultsStub();
    }

    void verifyQueryBehaviorForInteger(@NonNull Integer actualNumberOfResults) {
        assertThat(actualNumberOfResults).isNotNull();
        verify(storIOSQLite).get();
        verify(getResolverForNumberOfResults).performGet(storIOSQLite, query);
        assertThat(actualNumberOfResults).isSameAs(numberOfResults);
        verify(cursor).close();
        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyQueryBehaviorForInteger(@NonNull Observable<Integer> observable) {
        new ObservableBehaviorChecker<Integer>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Integer>() {
                    @Override
                    public void call(Integer numberOfResults) {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOSQLite).observeChanges();
                        verify(storIOSQLite).defaultScheduler();
                        verifyQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyQueryBehaviorForInteger(@NonNull Single<Integer> single) {
        new ObservableBehaviorChecker<Integer>()
                .observable(single.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Integer>() {
                    @Override
                    public void call(Integer numberOfResults) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyRawQueryBehaviorForInteger(@NonNull Integer actualNumberOfResults) {
        assertThat(actualNumberOfResults).isNotNull();
        verify(storIOSQLite).get();
        verify(getResolverForNumberOfResults).performGet(storIOSQLite, rawQuery);
        assertThat(actualNumberOfResults).isSameAs(numberOfResults);
        verify(cursor).close();
        verifyNoMoreInteractions(storIOSQLite, lowLevel, cursor);
    }

    void verifyRawQueryBehaviorForInteger(@NonNull Observable<Integer> observable) {
        new ObservableBehaviorChecker<Integer>()
                .observable(observable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Integer>() {
                    @Override
                    public void call(Integer numberOfResults) {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOSQLite).observeChanges();
                        verify(storIOSQLite).defaultScheduler();
                        verifyRawQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfObservable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }

    void verifyRawQueryBehaviorForInteger(@NonNull Single<Integer> single) {
        new ObservableBehaviorChecker<Integer>()
                .observable(single.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<Integer>() {
                    @Override
                    public void call(Integer numberOfResults) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyRawQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfObservable();

        assertThat(rawQuery.observesTables()).isNotNull();
    }
}
