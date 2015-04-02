package com.pushtorefresh.storio.contentprovider.operation.put;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;
import com.pushtorefresh.storio.operation.MapFunc;
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
class PutStub {

    final List<TestItem> items;
    final StorIOContentResolver storIOContentProvider;
    final StorIOContentResolver.Internal internal;
    final MapFunc<TestItem, ContentValues> mapFunc;
    final PutResolver<TestItem> putResolver;

    @NonNull
    public static PutStub newPutStubForOneItem() {
        return new PutStub(1);
    }

    @NonNull
    public static PutStub newPutStubForMultipleItems() {
        return new PutStub(3);
    }

    private PutStub(int numberOfTestItems) {
        items = new ArrayList<>(numberOfTestItems);

        for (int i = 0; i < numberOfTestItems; i++) {
            items.add(TestItem.newInstance());
        }

        storIOContentProvider = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentProvider.internal())
                .thenReturn(internal);

        when(storIOContentProvider.put())
                .thenReturn(new PreparedPut.Builder(storIOContentProvider));

        //noinspection unchecked
        putResolver = (PutResolver<TestItem>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOContentProvider), any(ContentValues.class)))
                .thenReturn(PutResult.newInsertResult(mock(Uri.class), TestItem.CONTENT_URI));

        //noinspection unchecked
        mapFunc = (MapFunc<TestItem, ContentValues>) mock(MapFunc.class);

        for (final TestItem item : items) {
            when(mapFunc.map(item))
                    .thenReturn(mock(ContentValues.class));
        }
    }

    void verifyBehaviorForMultiple(@NonNull PutCollectionResult<TestItem> putCollectionResult) {
        // only one call to storIOContentProvider.put() should occur
        verify(storIOContentProvider, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(items.size())).performPut(eq(storIOContentProvider), any(ContentValues.class));

        for (final TestItem item : items) {
            // map operation for each object should be called only once
            verify(mapFunc, times(1)).map(item);

            // putResolver's afterPut() callback should be called only once for each object
            verify(putResolver, times(1))
                    .afterPut(item, putCollectionResult.results().get(item));
        }
    }

    void verifyBehaviorForMultiple(@NonNull Observable<PutCollectionResult<TestItem>> putCollectionResultObservable) {
        new ObservableBehaviorChecker<PutCollectionResult<TestItem>>()
                .observable(putCollectionResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutCollectionResult<TestItem>>() {
                    @Override
                    public void call(PutCollectionResult<TestItem> putCollectionResult) {
                        verifyBehaviorForMultiple(putCollectionResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    // for first item
    void verifyBehaviorForOne(@NonNull PutResult putResult) {
        Map<TestItem, PutResult> putResultMap = new HashMap<>(1);
        putResultMap.put(items.get(0), putResult);
        verifyBehaviorForMultiple(PutCollectionResult.newInstance(putResultMap));
    }

    void verifyBehaviorForOne(@NonNull Observable<PutResult> putResultObservable) {
        new ObservableBehaviorChecker<PutResult>()
                .observable(putResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResult>() {
                    @Override
                    public void call(PutResult putResult) {
                        verifyBehaviorForOne(putResult);
                    }
                })
                .checkBehaviorOfObservable();
    }
}
