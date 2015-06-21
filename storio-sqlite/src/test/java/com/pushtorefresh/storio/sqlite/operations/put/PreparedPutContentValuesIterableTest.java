package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;

import org.junit.Test;

import rx.Observable;

public class PreparedPutContentValuesIterableTest {

    @Test
    public void putMultipleBlockingWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(true)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putMultipleObservableWithTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(true);

        final Observable<PutResults<ContentValues>> putResultsObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(true)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }

    @Test
    public void putMultipleBlockingWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);

        final PutResults<ContentValues> putResults = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(false)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putMultipleObservableWithoutTransaction() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues(false);

        final Observable<PutResults<ContentValues>> putResultsObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .useTransaction(false)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }
}
