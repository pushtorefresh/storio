package com.pushtorefresh.storio.contentresolver.operations.put;

import android.content.ContentValues;

import org.junit.Test;

import rx.Observable;

public class PreparedPutContentValuesIterableTest {

    @Test
    public void putContentValuesIterableBlocking() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();

        final PutResults<ContentValues> putResults = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putContentValuesIterableObservable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForMultipleContentValues();

        final Observable<PutResults<ContentValues>> putResultsObservable = putStub.storIOContentResolver
                .put()
                .contentValues(putStub.contentValues)
                .withPutResolver(putStub.putResolver)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }
}
