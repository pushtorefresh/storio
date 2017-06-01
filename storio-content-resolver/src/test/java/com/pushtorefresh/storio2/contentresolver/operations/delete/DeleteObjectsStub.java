package com.pushtorefresh.storio2.contentresolver.operations.delete;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.test.FlowableBehaviorChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
class DeleteObjectsStub {

    @NonNull
    final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final StorIOContentResolver.LowLevel lowLevel;

    @NonNull
    final List<TestItem> items;

    @NonNull
    final DeleteResolver<TestItem> deleteResolver;

    @NonNull
    private final ContentResolverTypeMapping<TestItem> typeMapping;

    @NonNull
    private final Map<TestItem, DeleteResult> testItemToDeleteResultMap;

    private final boolean withTypeMapping;

    @SuppressWarnings("unchecked")
    private DeleteObjectsStub(boolean withTypeMapping, int numberOfTestItems) {
        this.withTypeMapping = withTypeMapping;

        storIOContentResolver = mock(StorIOContentResolver.class);
        lowLevel = mock(StorIOContentResolver.LowLevel.class);

        when(storIOContentResolver.lowLevel())
                .thenReturn(lowLevel);

        when(storIOContentResolver.delete())
                .thenReturn(new PreparedDelete.Builder(storIOContentResolver));

        deleteResolver = mock(DeleteResolver.class);

        items = new ArrayList<TestItem>(numberOfTestItems);
        testItemToDeleteResultMap = new HashMap<TestItem, DeleteResult>(numberOfTestItems);

        typeMapping = mock(ContentResolverTypeMapping.class);

        if (withTypeMapping) {
            when(typeMapping.deleteResolver()).thenReturn(deleteResolver);
            when(lowLevel.typeMapping(TestItem.class)).thenReturn(typeMapping);
        }

        for (int i = 0; i < numberOfTestItems; i++) {
            final TestItem testItem = TestItem.newInstance();
            items.add(testItem);

            final Uri testItemUri = mock(Uri.class);

            final DeleteResult deleteResult = DeleteResult.newInstance(1, testItemUri);
            testItemToDeleteResultMap.put(testItem, deleteResult);

            when(deleteResolver.performDelete(storIOContentResolver, testItem))
                    .thenReturn(deleteResult);
        }
    }

    @NonNull
    static DeleteObjectsStub newInstanceForDeleteMultipleObjectsWithoutTypeMapping() {
        return new DeleteObjectsStub(false, 3);
    }

    @NonNull
    static DeleteObjectsStub newInstanceForDeleteMultipleObjectsWithTypeMapping() {
        return new DeleteObjectsStub(true, 3);
    }

    @NonNull
    static DeleteObjectsStub newInstanceForDeleteOneObjectWithoutTypeMapping() {
        return new DeleteObjectsStub(false, 1);
    }

    @NonNull
    static DeleteObjectsStub newInstanceForDeleteOneObjectWithTypeMapping() {
        return new DeleteObjectsStub(true, 1);
    }

    void verifyBehaviorForDeleteMultipleObjects(@NonNull DeleteResults<TestItem> deleteResults) {
        verify(storIOContentResolver).delete();

        if (withTypeMapping || items.size() > 1) {
            // should be called only once because of Performance!
            verify(storIOContentResolver).lowLevel();
        }

        // checks that delete was performed same amount of times as count of items
        verify(deleteResolver, times(items.size())).performDelete(eq(storIOContentResolver), any(TestItem.class));

        for (final TestItem testItem : items) {
            // checks that delete was performed for each item
            verify(deleteResolver, times(1)).performDelete(storIOContentResolver, testItem);

            final DeleteResult expectedDeleteResult = testItemToDeleteResultMap.get(testItem);

            // checks that delete results contains result of deletion of each item
            assertThat(deleteResults.results().get(testItem)).isEqualTo(expectedDeleteResult);
        }

        assertThat(deleteResults.results()).hasSize(items.size());

        if (withTypeMapping) {
            verify(lowLevel, times(items.size())).typeMapping(TestItem.class);
            verify(typeMapping, times(items.size())).deleteResolver();
        }

        verifyNoMoreInteractions(storIOContentResolver, lowLevel, typeMapping, deleteResolver);
    }

    void verifyBehaviorForDeleteMultipleObjects(@NonNull Flowable<DeleteResults<TestItem>> deleteResultsFlowable) {
        new FlowableBehaviorChecker<DeleteResults<TestItem>>()
                .flowable(deleteResultsFlowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<DeleteResults<TestItem>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull DeleteResults<TestItem> deleteResults) throws Exception {
                        verify(storIOContentResolver).defaultRxScheduler();
                        verifyBehaviorForDeleteMultipleObjects(deleteResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForDeleteMultipleObjects(@NonNull Single<DeleteResults<TestItem>> deleteResultsSingle) {
        new FlowableBehaviorChecker<DeleteResults<TestItem>>()
                .flowable(deleteResultsSingle.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<DeleteResults<TestItem>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull DeleteResults<TestItem> deleteResults) throws Exception {
                        verify(storIOContentResolver).defaultRxScheduler();
                        verifyBehaviorForDeleteMultipleObjects(deleteResults);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForDeleteMultipleObjects(@NonNull Completable completable) {
        verifyBehaviorForDeleteOneObject(completable.<DeleteResult>toFlowable());
    }

    void verifyBehaviorForDeleteOneObject(@NonNull DeleteResult deleteResult) {
        Map<TestItem, DeleteResult> deleteResultsMap = new HashMap<TestItem, DeleteResult>(1);
        deleteResultsMap.put(items.get(0), deleteResult);
        verifyBehaviorForDeleteMultipleObjects(DeleteResults.newInstance(deleteResultsMap));
    }

    void verifyBehaviorForDeleteOneObject(@NonNull Flowable<DeleteResult> deleteResultFlowable) {
        new FlowableBehaviorChecker<DeleteResult>()
                .flowable(deleteResultFlowable)
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<DeleteResult>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull DeleteResult deleteResult) throws Exception {
                        verify(storIOContentResolver).defaultRxScheduler();
                        verifyBehaviorForDeleteOneObject(deleteResult);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForDeleteOneObject(@NonNull Single<DeleteResult> deleteResultSingle) {
        new FlowableBehaviorChecker<DeleteResult>()
                .flowable(deleteResultSingle.toFlowable())
                .expectedNumberOfEmissions(1)
                .testAction(new Consumer<DeleteResult>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull DeleteResult deleteResult) throws Exception {
                        verify(storIOContentResolver).defaultRxScheduler();
                        verifyBehaviorForDeleteOneObject(deleteResult);
                    }
                })
                .checkBehaviorOfFlowable();
    }

    void verifyBehaviorForDeleteOneObject(@NonNull Completable completable) {
        verifyBehaviorForDeleteOneObject(completable.<DeleteResult>toFlowable());
    }
}
