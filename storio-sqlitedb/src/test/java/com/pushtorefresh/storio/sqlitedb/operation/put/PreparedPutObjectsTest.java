package com.pushtorefresh.storio.sqlitedb.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutObjectsTest {

    @Test
    public void putMultipleBlocking() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems();

        final PutCollectionResult<TestItem> putCollectionResult = putStub.storIOSQLiteDb
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultiple(putCollectionResult);
    }

    @Test
    public void putMultipleObservable() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems();

        final Observable<PutCollectionResult<TestItem>> putCollectionResultObservable = putStub.storIOSQLiteDb
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultiple(putCollectionResultObservable);
    }

    @Test
    public void putMultipleBlockingWithoutTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems(false);

        final PutCollectionResult<TestItem> putCollectionResult = putStub.storIOSQLiteDb
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .dontUseTransaction()
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultiple(putCollectionResult);
    }

    @Test
    public void putMultipleObservableWithoutTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems(false);

        final Observable<PutCollectionResult<TestItem>> putCollectionResultObservable = putStub.storIOSQLiteDb
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .dontUseTransaction()
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultiple(putCollectionResultObservable);
    }

    @Test
    public void putMultipleBlockingWithTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems(true);

        final PutCollectionResult<TestItem> putCollectionResult = putStub.storIOSQLiteDb
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .useTransactionIfPossible()
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultiple(putCollectionResult);
    }

    @Test
    public void putMultipleObservableWithTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems(true);

        final Observable<PutCollectionResult<TestItem>> putCollectionResultObservable = putStub.storIOSQLiteDb
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .useTransactionIfPossible()
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultiple(putCollectionResultObservable);
    }
}
