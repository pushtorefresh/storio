package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

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
    private final StorIOContentResolver.Internal internal;

    @NonNull
    final List<ContentValues> contentValues;

    @NonNull
    final PutResolver<ContentValues> putResolver;

    @NonNull
    private final Map<ContentValues, PutResult> contentValuesToPutResultsMap;

    @SuppressWarnings("unchecked")
    private PutContentValuesStub(int numberOfTestItems) {
        storIOContentResolver = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentResolver.internal())
                .thenReturn(internal);

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

    void verifyBehaviorForMultipleContentValues(@NonNull Observable<PutResults<ContentValues>> putResultsObservable) {
        new ObservableBehaviorChecker<PutResults<ContentValues>>()
                .observable(putResultsObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResults<ContentValues>>() {
                    @Override
                    public void call(PutResults<ContentValues> putResults) {
                        verifyBehaviorForMultipleContentValues(putResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOneContentValues(@NonNull PutResult putResult) {
        Map<ContentValues, PutResult> putResultsMap = new HashMap<ContentValues, PutResult>(1);
        putResultsMap.put(contentValues.get(0), putResult);
        verifyBehaviorForMultipleContentValues(PutResults.newInstance(putResultsMap));
    }

    void verifyBehaviorForOneContentValues(@NonNull Observable<PutResult> putResultObservable) {
        new ObservableBehaviorChecker<PutResult>()
                .observable(putResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResult>() {
                    @Override
                    public void call(PutResult putResult) {
                        verifyBehaviorForOneContentValues(putResult);
                    }
                })
                .checkBehaviorOfObservable();
    }
}
