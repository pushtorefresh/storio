package com.pushtorefresh.storio.sqlitedb.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutObjectTest {

    @Test
    public void putObjectBlocking() {
        final PutStub putStub = PutStub.newPutStubForOneItem();

        final PutResult putResult = putStub.storIOSQLiteDb
                .put()
                .object(putStub.testItems.get(0))
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForOne(putResult);
    }

    @Test
    public void putObjectObservable() {
        final PutStub putStub = PutStub.newPutStubForOneItem();

        final Observable<PutResult> putResultObservable = putStub.storIOSQLiteDb
                .put()
                .object(putStub.testItems.get(0))
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForOne(putResultObservable);
    }
}
