package com.pushtorefresh.storio.sqlitedb.operation.put;

import com.pushtorefresh.storio.sqlitedb.design.User;

import org.junit.Test;

public class PreparedPutObjectsTest {

    @Test
    public void putMultipleBlocking() {
        final PutMultipleStub putMultipleStub = new PutMultipleStub(true);

        final PutCollectionResult<User> putCollectionResult = putMultipleStub.storIOSQLiteDb
                .put()
                .objects(putMultipleStub.users)
                .withMapFunc(putMultipleStub.mapFunc)
                .withPutResolver(putMultipleStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putMultipleStub.verifyBehavior(putCollectionResult);
    }

    @Test
    public void putMultipleObservable() {
        final PutMultipleStub putMultipleStub = new PutMultipleStub(true);

        final PutCollectionResult<User> putCollectionResult = putMultipleStub.storIOSQLiteDb
                .put()
                .objects(putMultipleStub.users)
                .withMapFunc(putMultipleStub.mapFunc)
                .withPutResolver(putMultipleStub.putResolver)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        putMultipleStub.verifyBehavior(putCollectionResult);
    }

    @Test
    public void putMultipleBlockingWithoutTransaction() {
        final PutMultipleStub putMultipleStub = new PutMultipleStub(false);

        final PutCollectionResult<User> putCollectionResult = putMultipleStub.storIOSQLiteDb
                .put()
                .objects(putMultipleStub.users)
                .withMapFunc(putMultipleStub.mapFunc)
                .withPutResolver(putMultipleStub.putResolver)
                .dontUseTransaction()
                .prepare()
                .executeAsBlocking();

        putMultipleStub.verifyBehavior(putCollectionResult);
    }

    @Test
    public void putMultipleObservableWithoutTransaction() {
        final PutMultipleStub putMultipleStub = new PutMultipleStub(false);

        final PutCollectionResult<User> putCollectionResult = putMultipleStub.storIOSQLiteDb
                .put()
                .objects(putMultipleStub.users)
                .withMapFunc(putMultipleStub.mapFunc)
                .withPutResolver(putMultipleStub.putResolver)
                .dontUseTransaction()
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        putMultipleStub.verifyBehavior(putCollectionResult);
    }

    @Test
    public void putMultipleBlockingWithTransaction() {
        final PutMultipleStub putMultipleStub = new PutMultipleStub(true);

        final PutCollectionResult<User> putCollectionResult = putMultipleStub.storIOSQLiteDb
                .put()
                .objects(putMultipleStub.users)
                .withMapFunc(putMultipleStub.mapFunc)
                .withPutResolver(putMultipleStub.putResolver)
                .useTransactionIfPossible()
                .prepare()
                .executeAsBlocking();

        putMultipleStub.verifyBehavior(putCollectionResult);
    }

    @Test
    public void putMultipleObservableWithTransaction() {
        final PutMultipleStub putMultipleStub = new PutMultipleStub(true);

        final PutCollectionResult<User> putCollectionResult = putMultipleStub.storIOSQLiteDb
                .put()
                .objects(putMultipleStub.users)
                .withMapFunc(putMultipleStub.mapFunc)
                .withPutResolver(putMultipleStub.putResolver)
                .useTransactionIfPossible()
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        putMultipleStub.verifyBehavior(putCollectionResult);
    }
}
