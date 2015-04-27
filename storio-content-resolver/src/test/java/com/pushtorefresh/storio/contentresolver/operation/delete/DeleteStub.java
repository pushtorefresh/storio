package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
class DeleteStub {

    final List<TestItem> testItems;
    final StorIOContentResolver storIOContentResolver;
    final DeleteResolver<TestItem> deleteResolverForTestItems;
    final DeleteResolver<DeleteQuery> deleteResolverForQuery;
    final Map<TestItem, DeleteQuery> testItemDeleteQueryMap;
    private final StorIOContentResolver.Internal internal;
    private final Map<TestItem, DeleteResult> testItemToDeleteResultMap;

    @SuppressWarnings("unchecked")
    private DeleteStub(int numberOfTestItems) {
        storIOContentResolver = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        when(storIOContentResolver.delete())
                .thenReturn(new PreparedDelete.Builder(storIOContentResolver));

        deleteResolverForTestItems = mock(DeleteResolver.class);

        deleteResolverForQuery = mock(DeleteResolver.class);

        testItems = new ArrayList<TestItem>(numberOfTestItems);
        testItemToDeleteResultMap = new HashMap<TestItem, DeleteResult>(numberOfTestItems);

        testItemDeleteQueryMap = new HashMap<TestItem, DeleteQuery>(numberOfTestItems);

        for (int i = 0; i < numberOfTestItems; i++) {
            final TestItem testItem = TestItem.newInstance();
            testItems.add(testItem);

            final Uri testItemUri = mock(Uri.class);

            final DeleteResult deleteResult = DeleteResult.newInstance(1, testItemUri);
            testItemToDeleteResultMap.put(testItem, deleteResult);

            when(deleteResolverForTestItems.performDelete(storIOContentResolver, testItem))
                    .thenReturn(deleteResult);

            final DeleteQuery deleteQuery = new DeleteQuery.Builder()
                    .uri(testItemUri)
                    .build();

            testItemDeleteQueryMap.put(testItem, deleteQuery);

            when(deleteResolverForQuery.performDelete(storIOContentResolver, deleteQuery))
                    .thenReturn(deleteResult);
        }
    }

    @NonNull
    static DeleteStub newInstanceForDeleteByQuery() {
        return new DeleteStub(1);
    }

    @NonNull
    static DeleteStub newInstanceForDeleteMultipleObjects() {
        return new DeleteStub(3);
    }

    @NonNull
    static DeleteStub newInstanceForDeleteOneObject() {
        return new DeleteStub(1);
    }

    void verifyBehaviorForDeleteByQuery(@NonNull DeleteResult deleteResult) {
        final TestItem testItem = testItems.get(0);

        final DeleteQuery expectedDeleteQuery = testItemDeleteQueryMap.get(testItem);

        // checks that required delete was performed
        verify(deleteResolverForQuery, times(1)).performDelete(storIOContentResolver, expectedDeleteQuery);

        // only one call to DeleteResolver.performDelete() should occur
        verify(deleteResolverForQuery, times(1)).performDelete(any(StorIOContentResolver.class), any(DeleteQuery.class));

        // checks that actual delete result equals to expected
        final DeleteResult expectedDeleteResult = testItemToDeleteResultMap.get(testItem);
        assertEquals(expectedDeleteResult, deleteResult);
    }

    void verifyBehaviorForDeleteByQuery(@NonNull Observable<DeleteResult> deleteResultObservable) {
        new ObservableBehaviorChecker<DeleteResult>()
                .observable(deleteResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        verifyBehaviorForDeleteByQuery(deleteResult);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForDeleteMultipleObjects(@NonNull DeleteResults<TestItem> deleteResults) {
        // checks that delete was performed same amount of times as count of items
        verify(deleteResolverForTestItems, times(testItems.size())).performDelete(eq(storIOContentResolver), any(TestItem.class));

        for (final TestItem testItem : testItems) {
            // checks that delete was performed for each item
            verify(deleteResolverForTestItems, times(1)).performDelete(storIOContentResolver, testItem);

            final DeleteResult expectedDeleteResult = testItemToDeleteResultMap.get(testItem);

            // checks that delete results contains result of deletion of each item
            assertEquals(expectedDeleteResult, deleteResults.results().get(testItem));
        }

        assertEquals(testItems.size(), deleteResults.results().size());
    }

    void verifyBehaviorForDeleteMultipleObjects(@NonNull Observable<DeleteResults<TestItem>> deleteResultsObservable) {
        new ObservableBehaviorChecker<DeleteResults<TestItem>>()
                .observable(deleteResultsObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResults<TestItem>>() {
                    @Override
                    public void call(DeleteResults<TestItem> deleteResults) {
                        verifyBehaviorForDeleteMultipleObjects(deleteResults);
                    }
                })
                .checkBehaviorOfObservable();
    }

    void verifyBehaviorForDeleteOneObject(@NonNull DeleteResult deleteResult) {
        Map<TestItem, DeleteResult> deleteResultsMap = new HashMap<TestItem, DeleteResult>(1);
        deleteResultsMap.put(testItems.get(0), deleteResult);
        verifyBehaviorForDeleteMultipleObjects(DeleteResults.newInstance(deleteResultsMap));
    }

    void verifyBehaviorForDeleteOneObject(@NonNull Observable<DeleteResult> deleteResultObservable) {
        new ObservableBehaviorChecker<DeleteResult>()
                .observable(deleteResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        verifyBehaviorForDeleteOneObject(deleteResult);
                    }
                })
                .checkBehaviorOfObservable();
    }
}
