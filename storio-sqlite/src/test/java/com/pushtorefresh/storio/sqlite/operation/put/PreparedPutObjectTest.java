package com.pushtorefresh.storio.sqlite.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutObjectTest {

    @Test
    public void putObjectBlocking() {
        final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

        final PutResult putResult = putStub.storIOSQLite
                .put()
                .object(putStub.items.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForOneObject(putResult);
    }

    @Test
    public void putObjectObservable() {
        final PutObjectsStub putStub = PutObjectsStub.newPutStubForOneObjectWithoutTypeMapping();

        final Observable<PutResult> putResultObservable = putStub.storIOSQLite
                .put()
                .object(putStub.items.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForOneObject(putResultObservable);
    }
}
