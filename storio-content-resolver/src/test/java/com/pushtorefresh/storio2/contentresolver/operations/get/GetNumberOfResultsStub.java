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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetNumberOfResultsStub {
    @NonNull
    final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final StorIOContentResolver.LowLevel lowLevel;

    @NonNull
    final Query query;

    @NonNull
    final GetResolver<Integer> getResolverForNumberOfResults;

    @NonNull
    private final Cursor cursor;

    @NonNull
    private final Integer numberOfResults;

    private GetNumberOfResultsStub() {
        storIOContentResolver = mock(StorIOContentResolver.class);
        lowLevel = mock(StorIOContentResolver.LowLevel.class);

        when(storIOContentResolver.lowLevel())
                .thenReturn(lowLevel);

        query = Query
                .builder()
                .uri(mock(Uri.class))
                .build();

        //noinspection unchecked
        getResolverForNumberOfResults = mock(GetResolver.class);
        cursor = mock(Cursor.class);

        numberOfResults = 129;

        when(storIOContentResolver.get())
                .thenReturn(new PreparedGet.Builder(storIOContentResolver));

        when(storIOContentResolver.observeChangesOfUri(eq(query.uri()), eq(BackpressureStrategy.MISSING)))
                .thenReturn(Flowable.<Changes>empty());

        when(getResolverForNumberOfResults.performGet(storIOContentResolver, query))
                .thenReturn(cursor);

        when(getResolverForNumberOfResults.mapFromCursor(storIOContentResolver, cursor))
                .thenReturn(numberOfResults);
    }

    @NonNull
    static GetNumberOfResultsStub newInstance() {
        return new GetNumberOfResultsStub();
    }

    void verifyQueryBehaviorForInteger(@NonNull Integer actualNumberOfResults) {
        assertThat(actualNumberOfResults).isNotNull();
        verify(storIOContentResolver).get();
        verify(getResolverForNumberOfResults).performGet(storIOContentResolver, query);
        assertThat(actualNumberOfResults).isSameAs(numberOfResults);
        verify(cursor).close();
        verifyNoMoreInteractions(storIOContentResolver, lowLevel, cursor);
    }

    void verifyQueryBehaviorForInteger(@NonNull Flowable<Integer> flowable) {
        new FlowableBehaviorChecker<Integer>()
                .flowable(flowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<Integer>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Integer integer) throws Exception {
                        // Get Operation should be subscribed to changes of tables from Query
                        verify(storIOContentResolver).observeChangesOfUri(eq(query.uri()), eq(BackpressureStrategy.MISSING));
                        verify(storIOContentResolver).defaultRxScheduler();
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
                    public void accept(@io.reactivex.annotations.NonNull Integer integer) throws Exception {
                        verify(storIOContentResolver).defaultRxScheduler();
                        verifyQueryBehaviorForInteger(numberOfResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }
}
