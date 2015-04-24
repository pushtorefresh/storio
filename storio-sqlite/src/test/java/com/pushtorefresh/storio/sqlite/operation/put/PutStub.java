package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
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
    final StorIOSQLite storIOSQLite;
    final StorIOSQLite.Internal internal;
    final PutResolver<TestItem> putResolver;
    final boolean useTransaction;

    @SuppressWarnings("unchecked")
    private PutStub(boolean useTransaction, int numberOfItems) {
        this.useTransaction = useTransaction;

        testItems = new ArrayList<TestItem>(numberOfItems);

        for (int i = 0; i < numberOfItems; i++) {
            testItems.add(TestItem.newInstance());
        }

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        when(internal.transactionsSupported())
                .thenReturn(useTransaction);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        when(storIOSQLite.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLite));

        putResolver = (PutResolver<TestItem>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOSQLite), any(TestItem.class)))
                .thenReturn(PutResult.newInsertResult(1, TestItem.TABLE));
    }

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

    void verifyBehaviorForMultiple(@NonNull PutResults<TestItem> putResults) {
        // only one call to storIOSQLite.put() should occur
        verify(storIOSQLite, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(testItems.size())).performPut(eq(storIOSQLite), any(TestItem.class));

        for (final TestItem testItem : testItems) {
            verify(putResolver, times(1)).performPut(storIOSQLite, testItem);
        }

        if (useTransaction) {
            // if put() operation used transaction, only one notification should be thrown
            verify(internal, times(1))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE)));
        } else {
            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            verify(internal, times(testItems.size()))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE)));
        }
    }

    void verifyBehaviorForMultiple(@NonNull Observable<PutResults<TestItem>> putResultsObservable) {
        new ObservableBehaviorChecker<PutResults<TestItem>>()
                .observable(putResultsObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResults<TestItem>>() {
                    @Override
                    public void call(PutResults<TestItem> testItemPutResults) {
                        verifyBehaviorForMultiple(testItemPutResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOne(@NonNull PutResult putResult) {
        final Map<TestItem, PutResult> putResultsMap = new HashMap<TestItem, PutResult>(1);
        putResultsMap.put(testItems.get(0), putResult);
        verifyBehaviorForMultiple(PutResults.newInstance(putResultsMap));
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
