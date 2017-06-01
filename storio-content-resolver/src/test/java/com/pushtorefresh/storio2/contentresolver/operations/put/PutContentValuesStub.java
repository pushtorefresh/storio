package com.pushtorefresh.storio2.contentresolver.operations.put;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.test.FlowableBehaviorChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
class PutContentValuesStub {

    @NonNull
    final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final StorIOContentResolver.LowLevel lowLevel;

    @NonNull
    final List<ContentValues> contentValues;

    @NonNull
    final PutResolver<ContentValues> putResolver;

    @NonNull
    private final Map<ContentValues, PutResult> contentValuesToPutResultsMap;

    @SuppressWarnings("unchecked")
    private PutContentValuesStub(int numberOfTestItems) {
        storIOContentResolver = mock(StorIOContentResolver.class);
        lowLevel = mock(StorIOContentResolver.LowLevel.class);

        when(storIOContentResolver.lowLevel())
                .thenReturn(lowLevel);

        when(storIOContentResolver.put())
                .thenReturn(new PreparedPut.Builder(storIOContentResolver));

        contentValues = new ArrayList<ContentValues>(numberOfTestItems);
        contentValuesToPutResultsMap = new HashMap<ContentValues, PutResult>(numberOfTestItems);

        for (int i = 0; i < numberOfTestItems; i++) {
            final ContentValues cv = mock(ContentValues.class);
            contentValues.add(cv);
            contentValuesToPutResultsMap.put(cv, PutResult.newInsertResult(mock(Uri.class), TestItem.CONTENT_URI));
        }

        putResolver = (PutResolver<ContentValues>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOContentResolver), any(ContentValues.class)))
                .thenReturn(PutResult.newInsertResult(mock(Uri.class), TestItem.CONTENT_URI));

        for (final ContentValues cv : contentValues) {
            final PutResult putResult = PutResult.newInsertResult(mock(Uri.class), mock(Uri.class));

            contentValuesToPutResultsMap.put(cv, putResult);

            when(putResolver.performPut(storIOContentResolver, cv))
                    .thenReturn(putResult);
        }
    }

    @NonNull
    public static PutContentValuesStub newPutStubForOneContentValues() {
        return new PutContentValuesStub(1);
    }

    @NonNull
    public static PutContentValuesStub newPutStubForMultipleContentValues() {
        return new PutContentValuesStub(3);
    }

    void verifyBehaviorForMultipleContentValues(@NonNull PutResults<ContentValues> putResults) {
        // only one call to storIOContentResolver.put() should occur
        verify(storIOContentResolver, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(contentValues.size())).performPut(eq(storIOContentResolver), any(ContentValues.class));

        for (final ContentValues cv : contentValues) {
            // Put Operation should be performed once for each item
            verify(putResolver, times(1)).performPut(storIOContentResolver, cv);
        }
    }

    void verifyBehaviorForMultipleContentValues(@NonNull Flowable<PutResults<ContentValues>> putResultsFlowable) {
        new FlowableBehaviorChecker<PutResults<ContentValues>>()
                .flowable(putResultsFlowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResults<ContentValues>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull PutResults<ContentValues> putResults) throws Exception {
                        verifyBehaviorForMultipleContentValues(putResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForMultipleContentValues(@NonNull Single<PutResults<ContentValues>> putResultsSingle) {
        new FlowableBehaviorChecker<PutResults<ContentValues>>()
                .flowable(putResultsSingle.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResults<ContentValues>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull PutResults<ContentValues> putResults) throws Exception {
                        verifyBehaviorForMultipleContentValues(putResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForMultipleContentValues(@NonNull Completable completable) {
        verifyBehaviorForMultipleContentValues(completable.<PutResults<ContentValues>>toFlowable());
    }

    void verifyBehaviorForOneContentValues(@NonNull PutResult putResult) {
        Map<ContentValues, PutResult> putResultsMap = new HashMap<ContentValues, PutResult>(1);
        putResultsMap.put(contentValues.get(0), putResult);
        verifyBehaviorForMultipleContentValues(PutResults.newInstance(putResultsMap));
    }

    void verifyBehaviorForOneContentValues(@NonNull Flowable<PutResult> putResultFlowable) {
        new FlowableBehaviorChecker<PutResult>()
                .flowable(putResultFlowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResult>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull PutResult putResult) throws Exception {
                        verifyBehaviorForOneContentValues(putResult);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForOneContentValues(@NonNull Single<PutResult> putResultSingle) {
        new FlowableBehaviorChecker<PutResult>()
                .flowable(putResultSingle.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResult>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull PutResult putResult) throws Exception {
                        verifyBehaviorForOneContentValues(putResult);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForOneContentValues(@NonNull Completable completable) {
        verifyBehaviorForOneContentValues(completable.<PutResult>toFlowable());
    }
}
