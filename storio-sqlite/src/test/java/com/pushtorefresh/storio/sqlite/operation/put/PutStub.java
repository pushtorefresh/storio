package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
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
class PutStub {

    private enum Type {
        OBJECT,
        CONTENT_VALUES
    }

    final List<TestItem> testItems;
    final List<ContentValues> contentValues;
    final StorIOSQLite storIOSQLite;
    private final StorIOSQLite.Internal internal;
    final PutResolver<TestItem> putResolverForObjects;
    final PutResolver<ContentValues> putResolverForContentValues;
    private final boolean useTransaction;
    private final Type type;

    @SuppressWarnings("unchecked")
    private PutStub(Type type, boolean useTransaction, int numberOfItems) {
        this.type = type;
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        when(storIOSQLite.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLite));


        if (type == Type.OBJECT) {
            contentValues = null;
            putResolverForContentValues = null;

            testItems = new ArrayList<TestItem>(numberOfItems);

            for (int i = 0; i < numberOfItems; i++) {
                testItems.add(TestItem.newInstance());
            }

            putResolverForObjects = (PutResolver<TestItem>) mock(PutResolver.class);

            when(putResolverForObjects.performPut(eq(storIOSQLite), any(TestItem.class)))
                    .thenReturn(PutResult.newInsertResult(1, TestItem.TABLE));
        } else if (type == Type.CONTENT_VALUES) {
            testItems = null;
            putResolverForObjects = null;

            contentValues = new ArrayList<ContentValues>(numberOfItems);

            for (int i = 0; i < numberOfItems; i++) {
                contentValues.add(mock(ContentValues.class));
            }

            putResolverForContentValues = (PutResolver<ContentValues>) mock(PutResolver.class);

            when(putResolverForContentValues.performPut(eq(storIOSQLite), any(ContentValues.class)))
                    .thenReturn(PutResult.newInsertResult(1, TestItem.TABLE));
        } else {
            throw new IllegalStateException("Unsupported type " + type);
        }
    }

    @NonNull
    static PutStub newPutStubForOneObject() {
        return new PutStub(Type.OBJECT, false, 1);
    }

    @NonNull
    static PutStub newPutStubForMultipleObjects(boolean useTransaction) {
        return new PutStub(Type.OBJECT, useTransaction, 3);
    }

    @NonNull
    static PutStub newPutStubForOneContentValues() {
        return new PutStub(Type.CONTENT_VALUES, false, 1);
    }

    @NonNull
    static PutStub newPutStubForMultipleContentValues(boolean useTransaction) {
        return new PutStub(Type.CONTENT_VALUES, useTransaction, 3);
    }

    void verifyBehaviorForMultipleObjects(@NonNull PutResults<TestItem> putResults) {
        // only one call to storIOSQLite.put() should occur
        verify(storIOSQLite, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolverForObjects, times(testItems.size())).performPut(eq(storIOSQLite), any(TestItem.class));

        // each item should be "put"
        for (final TestItem testItem : testItems) {
            verify(putResolverForObjects, times(1)).performPut(storIOSQLite, testItem);
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
        verifyBehaviorForMultipleObjects(PutResults.newInstance(singletonMap(testItems.get(0), putResult)));
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

    void verifyBehaviorForMultipleContentValues(@NonNull PutResults<ContentValues> putResults) {
        // only one call to storIOSQLite.put() should occur
        verify(storIOSQLite, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolverForContentValues, times(contentValues.size())).performPut(eq(storIOSQLite), any(ContentValues.class));

        // each item should be "put"
        for (final ContentValues cv : contentValues) {
            verify(putResolverForContentValues, times(1)).performPut(storIOSQLite, cv);
        }

        verifyTransactionBehavior();
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
        verifyBehaviorForMultipleContentValues(PutResults.newInstance(singletonMap(contentValues.get(0), putResult)));
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

            final int numberOfItems;

            if (type == Type.OBJECT) {
                numberOfItems = testItems.size();
            } else if (type == Type.CONTENT_VALUES) {
                numberOfItems = contentValues.size();
            } else {
                throw new IllegalStateException("Unsupported type " + type);
            }

            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            verify(internal, times(numberOfItems))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE)));
        }
    }
}
