package com.pushtorefresh.storio.sqlite.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutObjectsTest {

    @Test
    public void putMultipleBlockingWithTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleObjects(true);

        final PutResults<TestItem> putResults = putStub.storIOSQLite
                .put()
                .objects(TestItem.class, putStub.testItems)
                .useTransaction(true)
                .withPutResolver(putStub.putResolverForObjects)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleObjects(putResults);
    }

    @Test
    public void putMultipleObservableWithTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleObjects(true);

        final Observable<PutResults<TestItem>> putResultsObservable = putStub.storIOSQLite
                .put()
                .objects(TestItem.class, putStub.testItems)
                .useTransaction(true)
                .withPutResolver(putStub.putResolverForObjects)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleObjects(putResultsObservable);
    }

    @Test
    public void putMultipleBlockingWithoutTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleObjects(false);

        final PutResults<TestItem> putResults = putStub.storIOSQLite
                .put()
                .objects(TestItem.class, putStub.testItems)
                .withPutResolver(putStub.putResolverForObjects)
                .useTransaction(false)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleObjects(putResults);
    }

    @Test
    public void putMultipleObservableWithoutTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleObjects(false);

        final Observable<PutResults<TestItem>> putResultsObservable = putStub.storIOSQLite
                .put()
                .objects(TestItem.class, putStub.testItems)
                .withPutResolver(putStub.putResolverForObjects)
                .useTransaction(false)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleObjects(putResultsObservable);
    }
}
