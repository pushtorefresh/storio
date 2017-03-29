package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;

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
    private final StorIOSQLite.Internal internal;

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
    private final Set<String> affectedTags = singleton("test_tag");

    @SuppressWarnings("unchecked")
    private PutObjectsStub(boolean withTypeMapping, boolean useTransaction, int numberOfItems) {
        this.withTypeMapping = withTypeMapping;
        this.useTransaction = useTransaction;

        storIOSQLite = mock(StorIOSQLite.class);
        internal = mock(StorIOSQLite.Internal.class);

        when(storIOSQLite.lowLevel())
                .thenReturn(internal);

        when(storIOSQLite.put())
                .thenReturn(new PreparedPut.Builder(storIOSQLite));

        items = new ArrayList<TestItem>(numberOfItems);
        itemsToPutResultsMap = new HashMap<TestItem, PutResult>(numberOfItems);

        for (int i = 0; i < numberOfItems; i++) {
            final TestItem testItem = TestItem.newInstance();
            items.add(testItem);

            final PutResult putResult = PutResult.newInsertResult(1, TestItem.TABLE, affectedTags);

            itemsToPutResultsMap.put(testItem, putResult);
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
        // should be called once because of Performance!
        verify(storIOSQLite).lowLevel();

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

        verifyTransactionBehavior();

        if (withTypeMapping) {
            // should be called for each item
            verify(internal, times(items.size())).typeMapping(TestItem.class);

            // should be called for each item
            verify(typeMapping, times(items.size())).putResolver();
        }

        verifyNoMoreInteractions(storIOSQLite, internal, typeMapping, putResolver);
    }

    void verifyBehaviorForMultipleObjects(@NonNull Observable<PutResults<TestItem>> putResultsObservable) {
        new ObservableBehaviorChecker<PutResults<TestItem>>()
                .observable(putResultsObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResults<TestItem>>() {
                    @Override
                    public void call(PutResults<TestItem> testItemPutResults) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyBehaviorForMultipleObjects(testItemPutResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForMultipleObjects(@NonNull Single<PutResults<TestItem>> putResultsSingle) {
        new ObservableBehaviorChecker<PutResults<TestItem>>()
                .observable(putResultsSingle.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResults<TestItem>>() {
                    @Override
                    public void call(PutResults<TestItem> testItemPutResults) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyBehaviorForMultipleObjects(testItemPutResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForMultipleObjects(@NonNull Completable completable) {
        verifyBehaviorForMultipleObjects(completable.<PutResults<TestItem>>toObservable());
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
                        verify(storIOSQLite).defaultScheduler();
                        verifyBehaviorForOneObject(putResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOneObject(@NonNull Single<PutResult> putResultSingle) {
        new ObservableBehaviorChecker<PutResult>()
                .observable(putResultSingle.toObservable())
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<PutResult>() {
                    @Override
                    public void call(PutResult putResult) {
                        verify(storIOSQLite).defaultScheduler();
                        verifyBehaviorForOneObject(putResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForOneObject(@NonNull Completable completable) {
        verifyBehaviorForOneObject(completable.<PutResult>toObservable());
    }

    private void verifyTransactionBehavior() {
        if (useTransaction) {
            verify(internal).beginTransaction();
            verify(internal).setTransactionSuccessful();
            verify(internal).endTransaction();

            // if put() operation used transaction, only one notification should be thrown
            verify(internal)
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE, affectedTags)));
        } else {
            verify(internal, never()).beginTransaction();
            verify(internal, never()).setTransactionSuccessful();
            verify(internal, never()).endTransaction();

            // if put() operation didn't use transaction,
            // number of notifications should be equal to number of objects
            verify(internal, times(items.size()))
                    .notifyAboutChanges(eq(Changes.newInstance(TestItem.TABLE, affectedTags)));
        }
    }
}
