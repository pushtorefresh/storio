package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;

import org.junit.Test;

import rx.Observable;

public class PreparedPutContentValuesIterableTest {

    @Test
    public void putMultipleBlockingWithTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleContentValues(true);

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolverForContentValues)
                .useTransaction(true)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putMultipleObservableWithTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleContentValues(true);

        final Observable<PutResults<ContentValues>> putResultsObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolverForContentValues)
                .useTransaction(true)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }

    @Test
    public void putMultipleBlockingWithoutTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleContentValues(false);

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolverForContentValues)
                .useTransaction(false)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putMultipleObservableWithoutTransaction() {
        final PutStub putStub = PutStub.newPutStubForMultipleContentValues(false);

        final Observable<PutResults<ContentValues>> putResultsObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolverForContentValues)
                .useTransaction(false)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }
}
