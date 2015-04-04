package com.pushtorefresh.storio.contentresolver.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutObjectsTest {

    @Test
    public void putObjectsBlocking() {
        final PutStub putStub = PutStub.newPutStubForMultipleObjects();

        final PutResults<TestItem> putResults = putStub.storIOContentResolver
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolverForObjects)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleObjects(putResults);
    }

    @Test
    public void putObjectsObservable() {
        final PutStub putStub = PutStub.newPutStubForMultipleObjects();

        final Observable<PutResults<TestItem>> putResultsObservable = putStub.storIOContentResolver
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolverForObjects)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleObjects(putResultsObservable);
    }
}
