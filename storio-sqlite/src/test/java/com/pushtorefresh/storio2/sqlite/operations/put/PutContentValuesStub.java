package com.pushtorefresh.storio2.sqlite.operations.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.test.FlowableBehaviorChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Java6Assertions.assertThat;
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
    private final Map<Changes, Integer> expectedNotifications;

    @NonNull
    private static final PutResultCreator DEFAULT_PUT_RESULT_CREATOR = new PutResultCreator() {
        @Override
        @NonNull
        public PutResult newPutResult() {
            return PutResult.newInsertResult(1, TestItem.TABLE, singleton("test_tag"));
        }
    };

    @NonNull
    private static final PutResultCreator MOCK_PUT_RESULT_CREATOR = new PutResultCreator() {
        @NonNull
        @Override
        public PutResult newPutResult() {
            return mock(PutResult.class);
        }
    };

    @SuppressWarnings("unchecked")
    private PutContentValuesStub(boolean useTransaction, int numberOfItems, @NonNull PutResultCreator putResultCreator) {
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        when(storIOSQLite.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLite));

        contentValues = new ArrayList<ContentValues>(numberOfItems);

        putResolver = (PutResolver<ContentValues>) mock(PutResolver.class);

        final List<PutResult> putResults = new ArrayList<PutResult>(contentValues.size());
        expectedNotifications = new HashMap<Changes, Integer>(contentValues.size());

        for (int i = 0; i < numberOfItems; i++) {
            final ContentValues cv = mock(ContentValues.class);
            this.contentValues.add(cv);

            final PutResult putResult = putResultCreator.newPutResult();

            putResults.add(putResult);

            when(putResolver.performPut(storIOSQLite, cv)).thenReturn(putResult);

            if (!useTransaction && (!putResult.affectedTables().isEmpty() || !putResult.affectedTags().isEmpty())) {
                Changes changes = Changes.newInstance(putResult.affectedTables(), putResult.affectedTags());
                Integer notificationsCount = expectedNotifications.get(changes);
                expectedNotifications.put(changes, notificationsCount == null ? 1 : notificationsCount + 1);
            }
        }

        if (useTransaction) {
            final Set<String> tables = new HashSet<String>();
            final Set<String> tags = new HashSet<String>();

            for (PutResult putResult : putResults) {
                tables.addAll(putResult.affectedTables());
                tags.addAll(putResult.affectedTags());
            }

            if (!tables.isEmpty() || !tags.isEmpty()) {
                expectedNotifications.put(Changes.newInstance(tables, tags), 1);
            }
        }
    }

    @NonNull
    static PutContentValuesStub newPutStubForEmptyCollectionWithTransaction() {
        return new PutContentValuesStub(true, 0, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutContentValuesStub newPutStubForEmptyCollectionWithoutTransaction() {
        return new PutContentValuesStub(false, 0, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutContentValuesStub newPutStubForOneContentValues() {
        return new PutContentValuesStub(false, 1, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutContentValuesStub newPutStubForOneContentValuesWithoutInsertsAndUpdates() {
        return new PutContentValuesStub(false, 1, MOCK_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutContentValuesStub newPutStubForMultipleContentValues(boolean useTransaction) {
        return new PutContentValuesStub(useTransaction, 3, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutContentValuesStub newPutStubForMultipleContentValuesWithoutInsertsAndUpdatesWithTransaction() {
        return new PutContentValuesStub(true, 3, MOCK_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutContentValuesStub newPutStubForMultipleContentValuesWithoutInsertsAndUpdatesWithoutTransaction() {
        return new PutContentValuesStub(false, 3, MOCK_PUT_RESULT_CREATOR);
    }

    void verifyBehaviorForMultipleContentValues(@Nullable PutResults<ContentValues> putResults) {
        assertThat(putResults).isNotNull();

        // only one call to storIOSQLite.put() should occur
        verify(storIOSQLite).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(contentValues.size())).performPut(eq(storIOSQLite), any(ContentValues.class));

        // each item should be "put"
        for (final ContentValues cv : contentValues) {
            verify(putResolver).performPut(storIOSQLite, cv);
        }

        verifyNotificationsAndTransactionBehavior();
    }

    void verifyBehaviorForMultipleContentValues(@NonNull Flowable<PutResults<ContentValues>> putResultsFlowable) {
        new FlowableBehaviorChecker<PutResults<ContentValues>>()
                .flowable(putResultsFlowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResults<ContentValues>>() {
                    @Override
                    public void accept(PutResults<ContentValues> putResults) {
                        verifyBehaviorForMultipleContentValues(putResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForMultipleContentValues(@NonNull Completable completable) {
        verifyBehaviorForMultipleContentValues(completable.<PutResults<ContentValues>>toFlowable());
    }

    void verifyBehaviorForMultipleContentValues(@NonNull Single<PutResults<ContentValues>> putResultsSingle) {
        new FlowableBehaviorChecker<PutResults<ContentValues>>()
                .flowable(putResultsSingle.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResults<ContentValues>>() {
                    @Override
                    public void accept(PutResults<ContentValues> putResults) {
                        verifyBehaviorForMultipleContentValues(putResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }


    void verifyBehaviorForOneContentValues(@Nullable PutResult putResult) {
        assertThat(putResult).isNotNull();
        verifyBehaviorForMultipleContentValues(PutResults.newInstance(singletonMap(contentValues.get(0), putResult)));
    }

    void verifyBehaviorForOneContentValues(@NonNull Flowable<PutResult> putResultFlowable) {
        new FlowableBehaviorChecker<PutResult>()
                .flowable(putResultFlowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResult>() {
                    @Override
                    public void accept(PutResult putResult) {
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
                    public void accept(PutResult putResult) {
                        verifyBehaviorForOneContentValues(putResult);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForOneContentValues(@NonNull Completable completable) {
        verifyBehaviorForOneContentValues(completable.<PutResult>toFlowable());
    }

    private void verifyNotificationsAndTransactionBehavior() {
        if (useTransaction) {
            verify(lowLevel).beginTransaction();
            verify(lowLevel).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            if (!expectedNotifications.isEmpty()) {
                assertThat(expectedNotifications).hasSize(1);
                Map.Entry<Changes, Integer> expectedNotification = expectedNotifications.entrySet().iterator().next();
                assertThat(expectedNotification.getValue()).isEqualTo(1);

                // if put() operation used transaction, only one notification should be thrown
                verify(lowLevel).notifyAboutChanges(expectedNotification.getKey());
            } else {
                verify(lowLevel, never()).notifyAboutChanges(any(Changes.class));
            }
        } else {
            verify(lowLevel, never()).beginTransaction();
            verify(lowLevel, never()).setTransactionSuccessful();
            verify(lowLevel, never()).endTransaction();

            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            for (Map.Entry<Changes, Integer> expectedNotification : expectedNotifications.entrySet()) {
                verify(lowLevel, times(expectedNotification.getValue()))
                        .notifyAboutChanges(expectedNotification.getKey());
            }
        }
    }


    private interface PutResultCreator {

        @NonNull
        PutResult newPutResult();
    }
}
