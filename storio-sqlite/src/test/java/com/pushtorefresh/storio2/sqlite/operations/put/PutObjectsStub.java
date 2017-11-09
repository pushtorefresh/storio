package com.pushtorefresh.storio2.sqlite.operations.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.test.FlowableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
class PutObjectsStub {

    @NonNull
    final StorIOSQLite storIOSQLite;

    @NonNull
    private final StorIOSQLite.LowLevel lowLevel;

    @NonNull
    final List<TestItem> items;

    @NonNull
    final PutResolver<TestItem> putResolver;

    @NonNull
    private final SQLiteTypeMapping<TestItem> typeMapping;

    @NonNull
    private final Map<TestItem, PutResult> itemsToPutResultsMap;

    private final boolean withTypeMapping, useTransaction;

    @NonNull
    private final Map<Changes, Integer> expectedNotifications;

    @NonNull
    private static final PutResultCreator DEFAULT_PUT_RESULT_CREATOR = new PutResultCreator() {
        @Override
        @NonNull
        public PutResult newPutResult(@NonNull TestItem testItem) {
            return PutResult.newInsertResult(1, TestItem.TABLE, singleton("test_tag"));
        }
    };

    @NonNull
    private static final PutResultCreator MOCK_PUT_RESULT_CREATOR = new PutResultCreator() {
        @Override
        @NonNull
        public PutResult newPutResult(@NonNull TestItem testItem) {
            return mock(PutResult.class);
        }
    };

    @SuppressWarnings("unchecked")
    private PutObjectsStub(
            boolean withTypeMapping,
            boolean useTransaction,
            int numberOfItems,
            @NonNull PutResultCreator putResultCreator
    ) {
        this.withTypeMapping = withTypeMapping;
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        lowLevel = mock(StorIOSQLite.LowLevel.class);

        when(storIOSQLite.lowLevel()).thenReturn(lowLevel);

        when(storIOSQLite.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLite));

        items = new ArrayList<TestItem>(numberOfItems);
        itemsToPutResultsMap = new HashMap<TestItem, PutResult>(numberOfItems);

        expectedNotifications = new HashMap<Changes, Integer>(items.size());

        for (int i = 0; i < numberOfItems; i++) {
            final TestItem testItem = TestItem.newInstance();
            items.add(testItem);

            final PutResult putResult = putResultCreator.newPutResult(testItem);
            itemsToPutResultsMap.put(testItem, putResult);

            if (!useTransaction && (!putResult.affectedTables().isEmpty() || !putResult.affectedTags().isEmpty())) {
                Changes changes = Changes.newInstance(putResult.affectedTables(), putResult.affectedTags());
                Integer notificationsCount = expectedNotifications.get(changes);
                expectedNotifications.put(changes, notificationsCount == null ? 1 : notificationsCount + 1);
            }
        }

        if (useTransaction) {
            Set<String> tables = new HashSet<String>();
            Set<String> tags = new HashSet<String>();
            for (PutResult putResult : itemsToPutResultsMap.values()) {
                tables.addAll(putResult.affectedTables());
                tags.addAll(putResult.affectedTags());
            }
            if (!tables.isEmpty() || !tags.isEmpty()) {
                expectedNotifications.put(Changes.newInstance(tables, tags), 1);
            }
        }

        putResolver = (PutResolver<TestItem>) mock(PutResolver.class);

        when(putResolver.performPut(eq(storIOSQLite), any(TestItem.class)))
                .thenAnswer(new Answer<PutResult>() {
                    @SuppressWarnings("SuspiciousMethodCalls")
                    @Override
                    public PutResult answer(InvocationOnMock invocation) throws Throwable {
                        return itemsToPutResultsMap.get(invocation.getArguments()[1]);
                    }
                });

        typeMapping = mock(SQLiteTypeMapping.class);

        if (withTypeMapping) {
            when(lowLevel.typeMapping(TestItem.class)).thenReturn(typeMapping);
            when(typeMapping.putResolver()).thenReturn(putResolver);
        }
    }

    @NonNull
    static PutObjectsStub newPutStubForEmptyCollectionWithoutTransaction() {
        return new PutObjectsStub(true, false, 0, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForEmptyCollectionWithTransaction() {
        return new PutObjectsStub(true, true, 0, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForOneObjectWithoutTypeMapping() {
        return new PutObjectsStub(false, false, 1, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForOneObjectWithTypeMapping() {
        return new PutObjectsStub(true, false, 1, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForOneObjectWithoutInsertsAndUpdatesWithoutTypeMapping() {
        return new PutObjectsStub(false, false, 1, MOCK_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForOneObjectWithoutInsertsAndUpdatesWithTypeMapping() {
        return new PutObjectsStub(true, false, 1, MOCK_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithoutTypeMappingWithTransaction() {
        return new PutObjectsStub(false, true, 3, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithTypeMappingWithTransaction() {
        return new PutObjectsStub(true, true, 3, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithoutTypeMappingWithoutTransaction() {
        return new PutObjectsStub(false, false, 3, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithTypeMappingWithoutTransaction() {
        return new PutObjectsStub(true, false, 3, DEFAULT_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithoutInsertsAndUpdatesWithTypeMappingWithTransaction() {
        return new PutObjectsStub(true, true, 3, MOCK_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithoutInsertsAndUpdatesWithoutTypeMappingWithTransaction() {
        return new PutObjectsStub(false, true, 3, MOCK_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithoutInsertsAndUpdatesWithTypeMappingWithoutTransaction() {
        return new PutObjectsStub(true, false, 3, MOCK_PUT_RESULT_CREATOR);
    }

    @NonNull
    static PutObjectsStub newPutStubForMultipleObjectsWithoutInsertsAndUpdatesWithoutTypeMappingWithoutTransaction() {
        return new PutObjectsStub(false, false, 3, MOCK_PUT_RESULT_CREATOR);
    }

    void verifyBehaviorForMultipleObjects(@Nullable PutResults<TestItem> putResults) {
        assertThat(putResults).isNotNull();

        // should be called once because of Performance!
        verify(storIOSQLite).lowLevel();

        // should be called once
        verify(storIOSQLite).interceptors();

        // only one call to storIOSQLite.put() should occur
        verify(storIOSQLite).put();

        // number of calls to putResolver's performPut() should be equal to number of objects
        verify(putResolver, times(items.size())).performPut(eq(storIOSQLite), any(TestItem.class));

        for (final TestItem testItem : items) {
            // put resolver should be invoked for each item
            verify(putResolver).performPut(storIOSQLite, testItem);

            final PutResult expectedPutResult = itemsToPutResultsMap.get(testItem);

            assertThat(putResults.results().get(testItem)).isEqualTo(expectedPutResult);
        }

        assertThat(putResults.results()).hasSize(itemsToPutResultsMap.size());

        verifyNotificationsAndTransactionBehavior();

        if (withTypeMapping) {
            // should be called for each item
            verify(lowLevel, times(items.size())).typeMapping(TestItem.class);

            // should be called for each item
            verify(typeMapping, times(items.size())).putResolver();
        }

        verifyNoMoreInteractions(storIOSQLite, lowLevel, typeMapping, putResolver);
    }

    void verifyBehaviorForMultipleObjects(@NonNull Flowable<PutResults<TestItem>> putResultsFlowable) {
        new FlowableBehaviorChecker<PutResults<TestItem>>()
                .flowable(putResultsFlowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResults<TestItem>>() {
                    @Override
                    public void accept(PutResults<TestItem> testItemPutResults) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyBehaviorForMultipleObjects(testItemPutResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForMultipleObjects(@NonNull Single<PutResults<TestItem>> putResultsSingle) {
        new FlowableBehaviorChecker<PutResults<TestItem>>()
                .flowable(putResultsSingle.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResults<TestItem>>() {
                    @Override
                    public void accept(PutResults<TestItem> testItemPutResults) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyBehaviorForMultipleObjects(testItemPutResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForMultipleObjects(@NonNull Completable completable) {
        verifyBehaviorForMultipleObjects(completable.<PutResults<TestItem>>toFlowable());
    }

    void verifyBehaviorForOneObject(@Nullable PutResult putResult) {
        assertThat(putResult).isNotNull();
        verifyBehaviorForMultipleObjects(PutResults.newInstance(singletonMap(items.get(0), putResult)));
    }

    void verifyBehaviorForOneObject(@NonNull Flowable<PutResult> putResultFlowable) {
        new FlowableBehaviorChecker<PutResult>()
                .flowable(putResultFlowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResult>() {
                    @Override
                    public void accept(PutResult putResult) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyBehaviorForOneObject(putResult);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForOneObject(@NonNull Single<PutResult> putResultSingle) {
        new FlowableBehaviorChecker<PutResult>()
                .flowable(putResultSingle.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<PutResult>() {
                    @Override
                    public void accept(PutResult putResult) {
                        verify(storIOSQLite).defaultRxScheduler();
                        verifyBehaviorForOneObject(putResult);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForOneObject(@NonNull Completable completable) {
        verifyBehaviorForOneObject(completable.<PutResult>toFlowable());
    }

    private void verifyNotificationsAndTransactionBehavior() {
        if (useTransaction) {
            verify(lowLevel).beginTransaction();
            verify(lowLevel).setTransactionSuccessful();
            verify(lowLevel).endTransaction();

            if (!expectedNotifications.isEmpty()) {
                assertThat(expectedNotifications).hasSize(1);
                final Map.Entry<Changes, Integer> expectedNotification = expectedNotifications.entrySet().iterator().next();
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
                verify(lowLevel, times(expectedNotification.getValue())).notifyAboutChanges(expectedNotification.getKey());
            }
        }
    }


    private interface PutResultCreator {

        @NonNull
        PutResult newPutResult(@NonNull TestItem testItem);
    }
}
