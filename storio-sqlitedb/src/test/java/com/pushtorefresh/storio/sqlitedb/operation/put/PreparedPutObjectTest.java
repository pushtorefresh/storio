package com.pushtorefresh.storio.sqlitedb.operation.put;

import org.junit.Test;

public class PreparedPutObjectTest {

    @Test
    public void putObjectBlocking() {
        final PutOneStub putOneStub = new PutOneStub();

        final PutResult putResult = putOneStub.storIOSQLiteDb
                .put()
                .object(putOneStub.user)
                .withMapFunc(putOneStub.mapFunc)
                .withPutResolver(putOneStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putOneStub.verifyBehavior(putResult);
    }

    @Test
    public void putObjectObservable() {
        final PutOneStub putOneStub = new PutOneStub();

        final PutResult putResult = putOneStub.storIOSQLiteDb
                .put()
                .object(putOneStub.user)
                .withMapFunc(putOneStub.mapFunc)
                .withPutResolver(putOneStub.putResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        putOneStub.verifyBehavior(putResult);
    }
}
