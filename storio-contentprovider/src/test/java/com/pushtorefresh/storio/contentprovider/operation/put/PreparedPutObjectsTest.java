package com.pushtorefresh.storio.contentprovider.operation.put;

import org.junit.Test;

import rx.Observable;
import rx.functions.Action1;

import static com.pushtorefresh.storio.test.StorIOAssert.assertThatObservableEmitsOnce;

public class PreparedPutObjectsTest {

    @Test
    public void putObjectsBlocking() {
        final PutStub putStub = new PutStub(3);

        final PutCollectionResult<TestItem> putCollectionResult = putStub.storIOContentProvider
                .put()
                .objects(putStub.items)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehavior(putCollectionResult);
    }

    @Test
    public void putObjectsObservable() {
        final PutStub putStub = new PutStub(3);

        final Observable<PutCollectionResult<TestItem>> putCollectionResultObservable = putStub.storIOContentProvider
                .put()
                .objects(putStub.items)
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .createObservable();

        assertThatObservableEmitsOnce(putCollectionResultObservable, new Action1<PutCollectionResult<TestItem>>() {
            @Override
            public void call(PutCollectionResult<TestItem> putCollectionResult) {
                putStub.verifyBehavior(putCollectionResult);
            }
        });
    }
}
