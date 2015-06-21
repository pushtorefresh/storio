package com.pushtorefresh.storio.operations.group;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.test.ObservableBehaviorChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// we don't want to violate DRY in tests
class GroupOperationStub {

    final List<PreparedOperation<?>> preparedOperations;
    final Map<PreparedOperation<?>, Object> results;

    private GroupOperationStub() {
        preparedOperations = new ArrayList<PreparedOperation<?>>();
        results = new HashMap<PreparedOperation<?>, Object>();

        for (int i = 0; i < 3; i++) {
            final PreparedOperation<?> preparedOperation = mock(PreparedOperation.class);
            final Object result = mock(Object.class);

            when(preparedOperation.executeAsBlocking())
                    .thenReturn(result);

            preparedOperations.add(preparedOperation);
            results.put(preparedOperation, result);
        }
    }

    @NonNull
    static GroupOperationStub newInstance() {
        return new GroupOperationStub();
    }

    void verifyBehaviorOfExecuteAsBlocking(@NonNull GroupOperationResults groupOperationResults) {
        assertEquals(results, groupOperationResults.results());

        for (PreparedOperation<?> preparedOperation : preparedOperations) {
            verify(preparedOperation, times(1)).executeAsBlocking();
            verify(preparedOperation, times(0)).createObservable();

            final Object expectedResultOfOperation = results.get(preparedOperation);
            final Object actualResultOfOperation = groupOperationResults.results().get(preparedOperation);

            assertEquals(expectedResultOfOperation, actualResultOfOperation);
        }
    }

    void verifyBehaviorOfObservable(@NonNull Observable<GroupOperationResults> groupOperationResultsObservable) {
        new ObservableBehaviorChecker<GroupOperationResults>()
                .observable(groupOperationResultsObservable)
                .expectedNumberOfEmissions(1)
                .testAction(new Action1<GroupOperationResults>() {
                    @Override
                    public void call(GroupOperationResults groupOperationResults) {
                        verifyBehaviorOfExecuteAsBlocking(groupOperationResults);
                    }
                })
                .checkBehaviorOfObservable();
    }
}
