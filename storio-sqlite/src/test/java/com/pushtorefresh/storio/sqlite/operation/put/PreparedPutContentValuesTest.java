package com.pushtorefresh.storio.sqlite.operation.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutContentValuesTest {

    @Test
    public void putContentValuesBlocking() {
        final PutStub putStub = PutStub.newPutStubForOneContentValues();

        final PutResult putResult = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolverForContentValues)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForOneContentValues(putResult);
    }

    @Test
    public void putContentValuesObservable() {
        final PutStub putStub = PutStub.newPutStubForOneContentValues();

        final Observable<PutResult> putResultObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolverForContentValues)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForOneContentValues(putResultObservable);
    }
}
