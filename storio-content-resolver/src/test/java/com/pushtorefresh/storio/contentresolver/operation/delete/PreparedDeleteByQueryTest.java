package com.pushtorefresh.storio.contentresolver.operation.delete;

import org.junit.Test;

import rx.Observable;

public class PreparedDeleteByQueryTest {

    @Test
    public void deleteBlocking() {
        final DeleteStub deleteStub = DeleteStub.newInstanceForDeleteByQuery();

        final DeleteResult deleteResult = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.testItemDeleteQueryMap.get(deleteStub.testItems.get(0)))
                .withDeleteResolver(deleteStub.deleteResolverForQuery)
                .prepare()
                .executeAsBlocking();

        deleteStub.verifyBehaviorForDeleteByQuery(deleteResult);
    }

    @Test
    public void deleteObservable() {
        final DeleteStub deleteStub = DeleteStub.newInstanceForDeleteByQuery();

        final Observable<DeleteResult> deleteResultObservable = deleteStub.storIOContentResolver
                .delete()
                .byQuery(deleteStub.testItemDeleteQueryMap.get(deleteStub.testItems.get(0)))
                .withDeleteResolver(deleteStub.deleteResolverForQuery)
                .prepare()
                .createObservable();

        deleteStub.verifyBehaviorForDeleteByQuery(deleteResultObservable);
    }
}
