package com.pushtorefresh.storio.sqlite.operations.put;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import rx.Observable;

@RunWith(Enclosed.class)
public class PreparedPutObjectTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldPutObjectWithoutTypeMappingBlocking() {
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
        public void shouldPutObjectWithoutTypeMappingAsObservable() {
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


}
