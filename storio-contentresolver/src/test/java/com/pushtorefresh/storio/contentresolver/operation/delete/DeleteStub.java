package com.pushtorefresh.storio.contentresolver.operation.delete;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// stub class to avoid violation of DRY in tests
class DeleteStub {

    final List<TestItem> testItems;
    final Map<TestItem, DeleteQuery> testItemToDeleteQueryMap;
    final StorIOContentResolver storIOContentResolver;
    final DeleteResolver deleteResolver;
    private final Map<DeleteQuery, DeleteResult> deleteQueryToDeleteResultMap;

    @NonNull
    static DeleteStub newInstanceForDeleteByQuery() {
        return new DeleteStub(1);
    }

    private DeleteStub(int numberOfTestItems) {
        storIOContentResolver = mock(StorIOContentResolver.class);

        when(storIOContentResolver.delete())
                .thenReturn(new PreparedDelete.Builder(storIOContentResolver));

        deleteResolver = mock(DeleteResolver.class);

        testItems = new ArrayList<>(numberOfTestItems);
        testItemToDeleteQueryMap = new HashMap<>(numberOfTestItems);
        deleteQueryToDeleteResultMap = new HashMap<>(numberOfTestItems);

        for (int i = 0; i < numberOfTestItems; i++) {
            final TestItem testItem = TestItem.newInstance();
            final DeleteQuery deleteQuery = mock(DeleteQuery.class);
            testItems.add(testItem);
            testItemToDeleteQueryMap.put(testItem, deleteQuery);

            final DeleteResult deleteResult = mock(DeleteResult.class);
            deleteQueryToDeleteResultMap.put(deleteQuery, deleteResult);

            when(deleteResolver.performDelete(storIOContentResolver, deleteQuery))
                    .thenReturn(deleteResult);
        }
    }

    void verifyBehaviorForOneObject(@NonNull DeleteResult deleteResult) {
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

    void verifyBehaviorForOneObject(@NonNull Observable<DeleteResult> deleteResultObservable) {
        new ObservableBehaviorChecker<DeleteResult>()
                .observable(deleteResultObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        verifyBehaviorForOneObject(deleteResult);
                    }
                })
                .checkBehaviorOfObservable();
    }
}
