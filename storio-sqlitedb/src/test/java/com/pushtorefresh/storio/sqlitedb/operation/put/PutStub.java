package com.pushtorefresh.storio.sqlitedb.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlitedb.Changes;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
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
    final List<TestItem> testItems;
    final StorIOSQLiteDb storIOSQLiteDb;
    final StorIOSQLiteDb.Internal internal;
    final MapFunc<TestItem, ContentValues> mapFunc;
    final PutResolver<TestItem> putResolver;
    final boolean useTransaction;

    @NonNull
    static PutStub newPutStubForOneItem() {
        return new PutStub(false, 1);
    }

    @NonNull
    static PutStub newPutStubForMultipleItems() {
        return new PutStub(true, 3);
    }

    @NonNull
    static PutStub newPutStubForMultipleItems(boolean useTransaction) {
        return new PutStub(useTransaction, 3);
    }

    private PutStub(boolean useTransaction, int numberOfItems) {
        this.useTransaction = useTransaction;

        testItems = new ArrayList<>(numberOfItems);

        for (int i = 0; i < numberOfItems; i++) {
            testItems.add(TestItem.newInstance());
        }

        storIOSQLiteDb = mock(StorIOSQLiteDb.class);
        internal = mock(StorIOSQLiteDb.Internal.class);

        when(internal.transactionsSupported())
                .thenReturn(useTransaction);

        when(storIOSQLiteDb.internal())
                .thenReturn(internal);

        when(storIOSQLiteDb.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLiteDb));

        //noinspection unchecked
        putResolver = (PutResolver<TestItem>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOSQLiteDb), any(ContentValues.class)))
                .thenReturn(PutResult.newInsertResult(1, TestItem.TABLE));

        //noinspection unchecked
        mapFunc = (MapFunc<TestItem, ContentValues>) mock(MapFunc.class);

        for (TestItem testItem : testItems) {
            when(mapFunc.map(testItem))
                    .thenReturn(mock(ContentValues.class));
        }
    }

    void verifyBehaviorForMultiple(@NonNull PutCollectionResult<TestItem> putCollectionResult) {
        // only one call to storIOSQLiteDb.put() should occur
        verify(storIOSQLiteDb, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(testItems.size())).performPut(eq(storIOSQLiteDb), any(ContentValues.class));

        for (final TestItem testItem : testItems) {
            // map operation for each object should be called only once
            verify(mapFunc, times(1)).map(testItem);

            // putResolver's afterPut() callback should be called only once for each object
            verify(putResolver, times(1))
                    .afterPut(testItem, putCollectionResult.results().get(testItem));
        }

        if (useTransaction) {
            // if put() operation used transaction, only one notification should be thrown
            verify(internal, times(1))
                    .notifyAboutChanges(eq(new Changes(TestItem.TABLE)));
        } else {
            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            verify(internal, times(testItems.size()))
                    .notifyAboutChanges(eq(new Changes(TestItem.TABLE)));
        }
    }

    void verifyBehaviorForMultiple(@NonNull Observable<PutCollectionResult<TestItem>> putCollectionResultObservable) {
        new ObservableBehaviorChecker<PutCollectionResult<TestItem>>()
                .observable(putCollectionResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutCollectionResult<TestItem>>() {
                    @Override
                    public void call(PutCollectionResult<TestItem> testItemPutCollectionResult) {
                        verifyBehaviorForMultiple(testItemPutCollectionResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOne(@NonNull PutResult putResult) {
        final Map<TestItem, PutResult> putResultMap = new HashMap<>(1);
        putResultMap.put(testItems.get(0), putResult);
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
