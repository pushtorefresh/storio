package com.pushtorefresh.storio.sqlite.operations.put;

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
class PutContentValuesStub {

    @NonNull
    final StorIOSQLite storIOSQLite;

    @NonNull
    private final StorIOSQLite.Internal internal;

    @NonNull
    final List<ContentValues> contentValues;

    @NonNull
    final PutResolver<ContentValues> putResolver;

    private final boolean useTransaction;

    @SuppressWarnings("unchecked")
    private PutContentValuesStub(boolean useTransaction, int numberOfItems) {
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.internal())
                .thenReturn(internal);

        when(storIOSQLite.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLite));

        contentValues = new ArrayList<ContentValues>(numberOfItems);

        for (int i = 0; i < numberOfItems; i++) {
            contentValues.add(mock(ContentValues.class));
        }

        putResolver = (PutResolver<ContentValues>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOSQLite), any(ContentValues.class)))
                .thenReturn(PutResult.newInsertResult(1, TestItem.TABLE));
    }

    @NonNull
    static PutContentValuesStub newPutStubForOneContentValues() {
        return new PutContentValuesStub(false, 1);
    }

    @NonNull
    static PutContentValuesStub newPutStubForMultipleContentValues(boolean useTransaction) {
        return new PutContentValuesStub(useTransaction, 3);
    }

    void verifyBehaviorForMultipleContentValues(@NonNull PutResults<ContentValues> putResults) {
        // only one call to storIOSQLite.put() should occur
        verify(storIOSQLite, times(1)).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(contentValues.size())).performPut(eq(storIOSQLite), any(ContentValues.class));

        // each item should be "put"
        for (final ContentValues cv : contentValues) {
            verify(putResolver, times(1)).performPut(storIOSQLite, cv);
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

            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            verify(internal, times(contentValues.size()))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE)));
        }
    }
}
