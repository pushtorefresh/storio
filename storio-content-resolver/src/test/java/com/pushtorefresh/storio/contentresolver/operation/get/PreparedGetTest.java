package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;

import org.junit.Test;

import java.util.List;

import rx.Observable;

public class PreparedGetTest {

    @Test
    public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForCursor(cursor);
    }

    @Test
    public void getListOfObjectsBlocking() {
        final GetStub getStub = new GetStub();

        final List<TestItem> testItems = getStub.storIOContentResolver
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForTestItems)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForList(testItems);
    }

    @Test
    public void getCursorObservable() {
        final GetStub getStub = new GetStub();

        final Observable<Cursor> cursorObservable = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .createObservable()
                .take(1);

        getStub.verifyQueryBehaviorForCursor(cursorObservable);
    }

    @Test
    public void getListOfObjectsObservable() {
        final GetStub getStub = new GetStub();

        final Observable<List<TestItem>> testItemsObservable = getStub.storIOContentResolver
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForTestItems)
                .prepare()
                .createObservable()
                .take(1);

        getStub.verifyQueryBehaviorForList(testItemsObservable);
    }
}
