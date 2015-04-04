package com.pushtorefresh.storio.contentresolver.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutObjectsTest {

    @Test
    public void putObjectsBlocking() {
        final PutStub putStub = PutStub.newPutStubForMultipleObjects();

        final PutCollectionResult<TestItem> putCollectionResult = putStub.storIOContentResolver
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolverForObjects)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleObjects(putCollectionResult);
    }

    @Test
    public void putObjectsObservable() {
        final PutStub putStub = PutStub.newPutStubForMultipleObjects();

        final Observable<PutCollectionResult<TestItem>> putCollectionResultObservable = putStub.storIOContentResolver
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolverForObjects)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleObjects(putCollectionResultObservable);
    }
}
