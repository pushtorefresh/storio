package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.operation.MapFunc;
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
    final Map<TestItem, DeleteQuery> testItemToDeleteQueryMap;
    final StorIOContentResolver storIOContentResolver;
    private final StorIOContentResolver.Internal internal;
    final DeleteResolver deleteResolver;
    final MapFunc<TestItem, DeleteQuery> mapFunc;
    private final Map<DeleteQuery, DeleteResult> deleteQueryToDeleteResultMap;

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

    @SuppressWarnings("unchecked")
    private DeleteStub(int numberOfTestItems) {
        storIOContentResolver = mock(StorIOContentResolver.class);
        internal = mock(StorIOContentResolver.Internal.class);

        when(storIOContentResolver.internal())
                .thenReturn(internal);

        when(storIOContentResolver.delete())
                .thenReturn(new PreparedDelete.Builder(storIOContentResolver));

        deleteResolver = mock(DeleteResolver.class);

        mapFunc = (MapFunc<TestItem, DeleteQuery>) mock(MapFunc.class);

        testItems = new ArrayList<TestItem>(numberOfTestItems);
        testItemToDeleteQueryMap = new HashMap<TestItem, DeleteQuery>(numberOfTestItems);
        deleteQueryToDeleteResultMap = new HashMap<DeleteQuery, DeleteResult>(numberOfTestItems);

        for (int i = 0; i < numberOfTestItems; i++) {
            final TestItem testItem = TestItem.newInstance();
            final DeleteQuery deleteQuery = mock(DeleteQuery.class);
            testItems.add(testItem);
            testItemToDeleteQueryMap.put(testItem, deleteQuery);

            when(mapFunc.map(testItem))
                    .thenReturn(deleteQuery);

            final DeleteResult deleteResult = mock(DeleteResult.class);
            deleteQueryToDeleteResultMap.put(deleteQuery, deleteResult);

            when(deleteResolver.performDelete(storIOContentResolver, deleteQuery))
                    .thenReturn(deleteResult);
        }
    }

    void verifyBehaviorForDeleteByQuery(@NonNull DeleteResult deleteResult) {
        final TestItem testItem = testItems.get(0);
        final DeleteQuery expectedDeleteQuery = testItemToDeleteQueryMap.get(testItem);

        // checks that required delete was performed
        verify(deleteResolver, times(1)).performDelete(storIOContentResolver, expectedDeleteQuery);

        // only one call to DeleteResolver.performDelete() should occur
        verify(deleteResolver, times(1)).performDelete(any(StorIOContentResolver.class), any(DeleteQuery.class));

        // checks that actual delete result equals to expected
        final DeleteResult expectedDeleteResult = deleteQueryToDeleteResultMap.get(expectedDeleteQuery);
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
        verify(deleteResolver, times(testItems.size())).performDelete(eq(storIOContentResolver), any(DeleteQuery.class));

        for (final TestItem testItem : testItems) {
            final DeleteQuery expectedDeleteQuery = testItemToDeleteQueryMap.get(testItem);

            // checks that delete was performed for each item
            verify(deleteResolver, times(1)).performDelete(storIOContentResolver, expectedDeleteQuery);

            final DeleteResult expectedDeleteResult = deleteQueryToDeleteResultMap.get(expectedDeleteQuery);

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
