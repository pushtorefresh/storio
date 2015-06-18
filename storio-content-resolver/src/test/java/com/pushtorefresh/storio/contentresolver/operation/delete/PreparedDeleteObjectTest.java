package com.pushtorefresh.storio.contentresolver.operation.delete;

import org.junit.Test;

import rx.Observable;

public class PreparedDeleteObjectTest {

    @Test
    public void deleteObjectBlocking() {
        final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObject();

        final DeleteResult deleteResult = deleteStub.storIOContentResolver
                .delete()
                .object(deleteStub.items.get(0))
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .executeAsBlocking();

        deleteStub.verifyBehaviorForDeleteOneObject(deleteResult);
    }

    @Test
    public void deleteObjectObservable() {
        final DeleteObjectsStub deleteStub = DeleteObjectsStub.newInstanceForDeleteOneObject();

        final Observable<DeleteResult> deleteResultObservable = deleteStub.storIOContentResolver
                .delete()
                .object(deleteStub.items.get(0))
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .createObservable();

        deleteStub.verifyBehaviorForDeleteOneObject(deleteResultObservable);
    }
}
