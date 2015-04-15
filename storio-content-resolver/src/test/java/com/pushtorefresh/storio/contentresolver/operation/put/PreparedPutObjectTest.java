package com.pushtorefresh.storio.contentresolver.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutObjectTest {

    @Test
    public void putObjectBlocking() {
        final PutStub putStub = PutStub.newPutStubForOneObject();

        final PutResult putResult = putStub.storIOContentResolver
                .put()
                .object(putStub.testItems.get(0))
                .withPutResolver(putStub.putResolverForObjects)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForOneObject(putResult);
    }

    @Test
    public void putObjectObservable() {
        final PutStub putStub = PutStub.newPutStubForOneObject();

        final Observable<PutResult> putResultObservable = putStub.storIOContentResolver
                .put()
                .object(putStub.testItems.get(0))
                .withPutResolver(putStub.putResolverForObjects)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForOneObject(putResultObservable);
    }
}
