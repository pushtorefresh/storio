package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import static java.util.Collections.singletonMap;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
class PutObjectsStub {

    @NonNull
    final StorIOSQLite storIOSQLite;

    @NonNull
    private final StorIOSQLite.Internal internal;

    @NonNull
    final List<TestItem> items;

    @NonNull
    final PutResolver<TestItem> putResolver;

    @NonNull
    private final SQLiteTypeMapping<TestItem> typeMapping;

    private final boolean withTypeMapping, useTransaction;

    @SuppressWarnings("unchecked")
    private PutObjectsStub(boolean withTypeMapping, boolean useTransaction, int numberOfItems) {
        this.withTypeMapping = withTypeMapping;
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        when(storIOSQLite.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLite));


        items = new ArrayList<TestItem>(numberOfItems);

        for (int i = 0; i < numberOfItems; i++) {
            items.add(TestItem.newInstance());
        }

        putResolver = (PutResolver<TestItem>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOSQLite), any(TestItem.class)))
                .thenReturn(PutResult.newInsertResult(1, TestItem.TABLE));

        typeMapping = mock(SQLiteTypeMapping.class);

        if (withTypeMapping) {
            when(internal.typeMapping(TestItem.class)).thenReturn(typeMapping);
            when(typeMapping.putResolver()).thenReturn(putResolver);
        }
    }

    @NonNull
    static PutObjectsStub newPutStubForOneObjectWithoutTypeMapping() {
        return new PutObjectsStub(false, false, 1);
    }

    @NonNull
    static PutObjectsStub newPutStubForOneObjectWithTypeMapping() {
        return new PutObjectsStub(true, false, 1);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithoutTypeMappingWithTransaction() {
        return new PutObjectsStub(false, true, 3);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithTypeMappingWithTransaction() {
        return new PutObjectsStub(true, true, 3);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithoutTypeMappingWithoutTransaction() {
        return new PutObjectsStub(false, false, 3);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithTypeMappingWithoutTransaction() {
        return new PutObjectsStub(true, false, 3);
    }

    void verifyBehaviorForMultipleObjects(@NonNull PutResults<TestItem> putResults) {
        // only one call to storIOSQLite.put() should occur
        verify(storIOSQLite, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(items.size())).performPut(eq(storIOSQLite), any(TestItem.class));

        // each item should be "put"
        for (final TestItem testItem : items) {
            verify(putResolver, times(1)).performPut(storIOSQLite, testItem);
        }

        verifyTransactionBehavior();
    }

    void verifyBehaviorForMultipleObjects(@NonNull Observable<PutResults<TestItem>> putResultsObservable) {
        new ObservableBehaviorChecker<PutResults<TestItem>>()
                .observable(putResultsObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResults<TestItem>>() {
                    @Override
                    public void call(PutResults<TestItem> testItemPutResults) {
                        verifyBehaviorForMultipleObjects(testItemPutResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOneObject(@NonNull PutResult putResult) {
        verifyBehaviorForMultipleObjects(PutResults.newInstance(singletonMap(items.get(0), putResult)));
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

    private void verifyTransactionBehavior() {
        if (useTransaction) {
            verify(internal, times(1)).beginTransaction();
            verify(internal, times(1)).setTransactionSuccessful();
            verify(internal, times(1)).endTransaction();

            // if put() operation used transaction, only one notification should be thrown
            verify(internal, times(1))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE)));
        } else {
            verify(internal, times(0)).beginTransaction();
            verify(internal, times(0)).setTransactionSuccessful();
            verify(internal, times(0)).endTransaction();

            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            verify(internal, times(items.size()))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE)));
        }
    }
}
