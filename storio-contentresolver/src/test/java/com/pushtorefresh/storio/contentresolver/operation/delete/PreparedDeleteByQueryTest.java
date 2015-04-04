package com.pushtorefresh.storio.contentresolver.operation.delete;

import org.junit.Test;

import rx.Observable;

public class PreparedDeleteByQueryTest {

    @Test
    public void deleteBlocking() {
        final DeleteStub deleteStub = DeleteStub.newInstanceForDeleteByQuery();

        final DeleteResult deleteResult = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.testItemToDeleteQueryMap.get(deleteStub.testItems.get(0)))
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .executeAsBlocking();

        deleteStub.verifyBehaviorForOneObject(deleteResult);
    }

    @Test
    public void deleteObservable() {
        final DeleteStub deleteStub = DeleteStub.newInstanceForDeleteByQuery();

        final Observable<DeleteResult> deleteResultObservable = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.testItemToDeleteQueryMap.get(deleteStub.testItems.get(0)))
                .withDeleteResolver(deleteStub.deleteResolver)
                .prepare()
                .createObservable();

        deleteStub.verifyBehaviorForOneObject(deleteResultObservable);
    }
}
