package com.pushtorefresh.storio.contentprovider.operation.put;

import org.junit.Test;

import rx.Observable;
import rx.functions.Action1;

import static com.pushtorefresh.storio.test.StorIOAssert.assertThatObservableEmitsOnce;

public class PreparedPutObjectTest {

    @Test
    public void putObjectBlocking() {
        final PutStub putStub = new PutStub(1);

        final PutResult putResult = putStub.storIOContentProvider
                .put()
                .object(putStub.items.get(0))
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehavior(putResult);
    }

    @Test
    public void putObjectObservable() {
        final PutStub putStub = new PutStub(1);

        final Observable<PutResult> putResultObservable = putStub.storIOContentProvider
                .put()
                .object(putStub.items.get(0))
                .withMapFunc(putStub.mapFunc)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .createObservable();

        assertThatObservableEmitsOnce(putResultObservable, new Action1<PutResult>() {
            @Override
            public void call(PutResult putResult) {
                putStub.verifyBehavior(putResult);
            }
        });
    }
}
