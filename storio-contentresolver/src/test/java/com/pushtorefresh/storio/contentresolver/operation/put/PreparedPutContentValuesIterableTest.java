package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class PreparedPutContentValuesIterableTest {

    @Test
    public void putContentValuesIterableBlocking() {
        final PutStub putStub = PutStub.newPutStubForMultipleContentValues();

        final List<ContentValues> contentValuesList = new ArrayList<>();

        for (final TestItem testItem : putStub.testItems) {
            contentValuesList.add(putStub.mapFunc.map(testItem));
        }

        final PutResults<ContentValues> putResults = putStub.storIOContentResolver
                .put()
                .contentValues(contentValuesList)
                .withPutResolver(putStub.putResolverForContentValues)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForMultipleContentValues(putResults);
    }

    @Test
    public void putContentValuesIterableObservable() {
        final PutStub putStub = PutStub.newPutStubForMultipleContentValues();

        final List<ContentValues> contentValuesList = new ArrayList<>();

        for (final TestItem testItem : putStub.testItems) {
            contentValuesList.add(putStub.mapFunc.map(testItem));
        }

        final Observable<PutResults<ContentValues>> putResultsObservable = putStub.storIOContentResolver
                .put()
                .contentValues(contentValuesList)
                .withPutResolver(putStub.putResolverForContentValues)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForMultipleContentValues(putResultsObservable);
    }
}
