package com.pushtorefresh.storio.operation.group;

import org.junit.Test;

import rx.Observable;

public class PreparedGroupOperationTest {

    @Test
    public void executeAsBlocking() {
        final GroupOperationStub groupOperationStub = GroupOperationStub.newInstance();

        final GroupOperationResults groupOperationResults = new PreparedGroupOperation.Builder()
                .addOperations(groupOperationStub.preparedOperations)
                .prepare()
                .executeAsBlocking();

        groupOperationStub.verifyBehaviorOfExecuteAsBlocking(groupOperationResults);
    }

    @Test
    public void createObservable() {
        final GroupOperationStub groupOperationStub = GroupOperationStub.newInstance();

        final Observable<GroupOperationResults> groupOperationResultsObservable = new PreparedGroupOperation.Builder()
                .addOperations(groupOperationStub.preparedOperations)
                .prepare()
                .createObservable();

        groupOperationStub.verifyBehaviorOfObservable(groupOperationResultsObservable);
    }
}
