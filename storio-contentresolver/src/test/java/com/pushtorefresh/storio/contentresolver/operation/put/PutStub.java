package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
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

    private enum TypeOfItems {
        OBJECTS,
        CONTENT_VALUES
    }

    final TypeOfItems typeOfItems;
    final List<TestItem> testItems;
    final StorIOContentResolver storIOContentResolver;
    final StorIOContentResolver.Internal internal;
    final MapFunc<TestItem, ContentValues> mapFunc;
    final PutResolver<TestItem> putResolverForObjects;
    final PutResolver<ContentValues> putResolverForContentValues;
    final Map<TestItem, ContentValues> testItemsToContentValuesMap;

    @NonNull
    public static PutStub newPutStubForOneObject() {
        return new PutStub(TypeOfItems.OBJECTS, 1);
    }

    @NonNull
    public static PutStub newPutStubForMultipleObjects() {
        return new PutStub(TypeOfItems.OBJECTS, 3);
    }

    @NonNull
    public static PutStub newPutStubForOneContentValues() {
        return new PutStub(TypeOfItems.CONTENT_VALUES, 1);
    }

    @SuppressWarnings("unchecked")
    private PutStub(@NonNull TypeOfItems typeOfItems, int numberOfTestItems) {
        this.typeOfItems = typeOfItems;

        testItems = new ArrayList<>(numberOfTestItems);
        testItemsToContentValuesMap = new HashMap<>(numberOfTestItems);

        for (int i = 0; i < numberOfTestItems; i++) {
            final TestItem testItem = TestItem.newInstance();
            testItems.add(testItem);
            testItemsToContentValuesMap.put(testItem, mock(ContentValues.class));
        }

        storIOContentResolver = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        when(storIOContentResolver.put())
                .thenReturn(new PreparedPut.Builder(storIOContentResolver));

        if (typeOfItems.equals(TypeOfItems.OBJECTS)) {
            putResolverForObjects = (PutResolver<TestItem>) mock(PutResolver.class);

            when(putResolverForObjects.performPut(eq(storIOContentResolver), any(ContentValues.class)))
                    .thenReturn(PutResult.newInsertResult(mock(Uri.class), TestItem.CONTENT_URI));
        } else {
            putResolverForObjects = null;
        }

        if (typeOfItems.equals(TypeOfItems.CONTENT_VALUES)) {
            putResolverForContentValues = (PutResolver<ContentValues>) mock(PutResolver.class);

            when(putResolverForContentValues.performPut(eq(storIOContentResolver), any(ContentValues.class)))
                    .thenReturn(PutResult.newInsertResult(mock(Uri.class), TestItem.CONTENT_URI));
        } else {
            putResolverForContentValues = null;
        }

        mapFunc = (MapFunc<TestItem, ContentValues>) mock(MapFunc.class);

        for (final TestItem testItem : testItems) {
            when(mapFunc.map(testItem))
                    .thenReturn(testItemsToContentValuesMap.get(testItem));
        }
    }

    void verifyBehaviorForMultipleObjects(@NonNull PutCollectionResult<TestItem> putCollectionResult) {
        // only one call to storIOContentResolver.put() should occur
        verify(storIOContentResolver, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolverForObjects, times(testItems.size())).performPut(eq(storIOContentResolver), any(ContentValues.class));

        for (final TestItem testItem : testItems) {
            // map operation for each object should be called only once
            verify(mapFunc, times(1)).map(testItem);

            // callback after Put Operation should be called once for each item
            verify(putResolverForObjects, times(1))
                    .afterPut(testItem, putCollectionResult.results().get(testItem));
        }
    }

    void verifyBehaviorForMultipleObjects(@NonNull Observable<PutCollectionResult<TestItem>> putCollectionResultObservable) {
        new ObservableBehaviorChecker<PutCollectionResult<TestItem>>()
                .observable(putCollectionResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutCollectionResult<TestItem>>() {
                    @Override
                    public void call(PutCollectionResult<TestItem> putCollectionResult) {
                        verifyBehaviorForMultipleObjects(putCollectionResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOneObject(@NonNull PutResult putResult) {
        Map<TestItem, PutResult> putResultMap = new HashMap<>(1);
        putResultMap.put(testItems.get(0), putResult);
        verifyBehaviorForMultipleObjects(PutCollectionResult.newInstance(putResultMap));
    }

    void verifyBehaviorForOneObject(@NonNull Observable<PutResult> putResultObservable) {
        new ObservableBehaviorChecker<PutResult>()
                .observable(putResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResult>() {
                    @Override
                    public void call(PutResult putResult) {
                        verifyBehaviorForOneObject(putResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForMultipleContentValues(@NonNull PutCollectionResult<ContentValues> putCollectionResult) {
        // only one call to storIOContentResolver.put() should occur
        verify(storIOContentResolver, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolverForContentValues, times(testItems.size())).performPut(eq(storIOContentResolver), any(ContentValues.class));

        for (final TestItem testItem : testItems) {
            final ContentValues contentValues = testItemsToContentValuesMap.get(testItem);

            // Put Operation should be performed once for each item
            verify(putResolverForContentValues, times(1))
                    .performPut(storIOContentResolver, contentValues);

            // callback after Put Operation should be called once for each item
            verify(putResolverForContentValues, times(1))
                    .afterPut(contentValues, putCollectionResult.results().get(contentValues));
        }
    }

    void verifyBehaviorForMultipleContentValues(@NonNull Observable<PutCollectionResult<ContentValues>> putCollectionResultObservable) {
        new ObservableBehaviorChecker<PutCollectionResult<ContentValues>>()
                .observable(putCollectionResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutCollectionResult<ContentValues>>() {
                    @Override
                    public void call(PutCollectionResult<ContentValues> putCollectionResult) {
                        verifyBehaviorForMultipleContentValues(putCollectionResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOneContentValues(@NonNull PutResult putResult) {
        Map<ContentValues, PutResult> putResultMap = new HashMap<>(1);
        putResultMap.put(mapFunc.map(testItems.get(0)), putResult);
        verifyBehaviorForMultipleContentValues(PutCollectionResult.newInstance(putResultMap));
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
