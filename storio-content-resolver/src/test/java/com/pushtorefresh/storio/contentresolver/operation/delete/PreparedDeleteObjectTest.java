package com.pushtorefresh.storio.contentresolver.operation.delete;

import org.junit.Test;

import rx.Observable;

public class PreparedDeleteObjectTest {

    @Test
    public void deleteObjectBlocking() {
        final DeleteStub deleteStub = DeleteStub.newInstanceForDeleteOneObject();

        final DeleteResult deleteResult = deleteStub.storIOContentResolver
                .delete()
                .object(deleteStub.testItems.get(0))
                .withMapFunc(deleteStub.mapFunc)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .executeAsBlocking();

        deleteStub.verifyBehaviorForDeleteOneObject(deleteResult);
    }

    @Test
    public void deleteObjectObservable() {
        final DeleteStub deleteStub = DeleteStub.newInstanceForDeleteOneObject();

        final Observable<DeleteResult> deleteResultObservable = deleteStub.storIOContentResolver
                .delete()
                .object(deleteStub.testItems.get(0))
                .withMapFunc(deleteStub.mapFunc)
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .createObservable();

        deleteStub.verifyBehaviorForDeleteOneObject(deleteResultObservable);
    }
}
