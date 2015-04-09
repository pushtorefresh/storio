package com.pushtorefresh.storio.sqlite.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutObjectsTest {

    @Test
    public void putMultipleBlocking() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems();

        final PutResults<TestItem> putResults = putStub.storIOSQLite
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultiple(putResults);
    }

    @Test
    public void putMultipleObservable() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems();

        final Observable<PutResults<TestItem>> putResultsObservable = putStub.storIOSQLite
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultiple(putResultsObservable);
    }

    @Test
    public void putMultipleBlockingWithoutTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems(false);

        final PutResults<TestItem> putResults = putStub.storIOSQLite
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .dontUseTransaction()
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultiple(putResults);
    }

    @Test
    public void putMultipleObservableWithoutTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems(false);

        final Observable<PutResults<TestItem>> putResultsObservable = putStub.storIOSQLite
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .dontUseTransaction()
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultiple(putResultsObservable);
    }

    @Test
    public void putMultipleBlockingWithTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems(true);

        final PutResults<TestItem> putResults = putStub.storIOSQLite
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .useTransactionIfPossible()
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultiple(putResults);
    }

    @Test
    public void putMultipleObservableWithTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleItems(true);

        final Observable<PutResults<TestItem>> putResultsObservable = putStub.storIOSQLite
                .put()
                .objects(putStub.testItems)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .useTransactionIfPossible()
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultiple(putResultsObservable);
    }
}
