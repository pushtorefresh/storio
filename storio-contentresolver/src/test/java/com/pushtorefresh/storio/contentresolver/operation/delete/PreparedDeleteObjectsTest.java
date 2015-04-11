package com.pushtorefresh.storio.contentresolver.operation.delete;

import org.junit.Test;

import rx.Observable;

public class PreparedDeleteObjectsTest {

    @Test
    public void deleteObjectsBlocking() {
        final DeleteStub deleteStub = DeleteStub.newInstanceForDeleteMultipleObjects();

        final DeleteResults<TestItem> deleteResults = deleteStub.storIOContentResolver
                .delete()
                .objects(TestItem.class, deleteStub.testItems)
                .withMapFunc(deleteStub.mapFunc)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .executeAsBlocking();

        deleteStub.verifyBehaviorForDeleteMultipleObjects(deleteResults);
    }

    @Test
    public void deleteObjectsObservable() {
        final DeleteStub deleteStub = DeleteStub.newInstanceForDeleteMultipleObjects();

        final Observable<DeleteResults<TestItem>> deleteResultsObservable = deleteStub.storIOContentResolver
                .delete()
                .objects(TestItem.class, deleteStub.testItems)
                .withMapFunc(deleteStub.mapFunc)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .createObservable();

        deleteStub.verifyBehaviorForDeleteMultipleObjects(deleteResultsObservable);
    }
}
