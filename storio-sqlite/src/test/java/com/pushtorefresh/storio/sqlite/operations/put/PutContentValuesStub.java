package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
class PutContentValuesStub {

    @NonNull
    public final StorIOSQLite storIOSQLite;

    @NonNull
    public final StorIOSQLite.LowLevel lowLevel;

    @NonNull
    final List<ContentValues> contentValues;

    @NonNull
    final PutResolver<ContentValues> putResolver;

    private final boolean useTransaction;

    @NonNull
    private final Set<String> affectedTags = singleton("test_tag");

    @SuppressWarnings("unchecked")
    private PutContentValuesStub(boolean useTransaction, int numberOfItems) {
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        when(storIOSQLite.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLite));

        contentValues = new ArrayList<ContentValues>(numberOfItems);

        for (int i = 0; i < numberOfItems; i++) {
            contentValues.add(mock(ContentValues.class));
        }

        putResolver = (PutResolver<ContentValues>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOSQLite), any(ContentValues.class)))
                .thenReturn(PutResult.newInsertResult(1, TestItem.TABLE, affectedTags));
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
        verify(storIOSQLite).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(contentValues.size())).performPut(eq(storIOSQLite), any(ContentValues.class));

        // each item should be "put"
        for (final ContentValues cv : contentValues) {
            verify(putResolver).performPut(storIOSQLite, cv);
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

    void verifyBehaviorForMultipleContentValues(@NonNull Completable completable) {
        verifyBehaviorForMultipleContentValues(completable.<PutResults<ContentValues>>toObservable());
    }

    void verifyBehaviorForMultipleContentValues(@NonNull Single<PutResults<ContentValues>> putResultsSingle) {
        new ObservableBehaviorChecker<PutResults<ContentValues>>()
                .observable(putResultsSingle.toObservable())
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

    void verifyBehaviorForOneContentValues(@NonNull Single<PutResult> putResultSingle) {
        new ObservableBehaviorChecker<PutResult>()
                .observable(putResultSingle.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResult>() {
                    @Override
                    public void call(PutResult putResult) {
                        verifyBehaviorForOneContentValues(putResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOneContentValues(@NonNull Completable completable) {
        verifyBehaviorForOneContentValues(completable.<PutResult>toObservable());
    }

    private void verifyTransactionBehavior() {
        if (useTransaction) {
            verify(lowLevel).beginTransaction();
            verify(lowLevel).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            // if put() operation used transaction, only one notification should be thrown
            verify(lowLevel)
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE, affectedTags)));
        } else {
            verify(lowLevel, never()).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel, never()).endTransaction();

            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            verify(lowLevel, times(contentValues.size()))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE, affectedTags)));
        }
    }
}
