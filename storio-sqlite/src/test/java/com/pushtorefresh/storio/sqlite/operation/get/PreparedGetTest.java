package com.pushtorefresh.storio.sqlite.operation.get;

import android.database.Cursor;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PreparedGetTest {

    @Test
    public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOSQLite
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

        final List<TestItem> testItems = getStub.storIOSQLite
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForObject)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForList(testItems);
    }

    @Test
    public void getCursorObservable() {
        final GetStub getStub = new GetStub();

        final Observable<Cursor> cursorObservable = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .createObservable();

        getStub.verifyQueryBehaviorForCursor(cursorObservable);
    }

    @Test
    public void getCursorObservableStreamTakeOne() {
        final GetStub getStub = new GetStub();

        final Observable<Cursor> cursorObservable = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .createObservableStream()
                .take(1);

        getStub.verifyQueryBehaviorForCursor(cursorObservable);

        verify(getStub.storIOSQLite, times(1)).observeChangesInTables(eq(Collections.singleton(getStub.query.table)));
    }


    @Test
    public void getListOfObjectsObservable() {
        final GetStub getStub = new GetStub();

        final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForObject)
                .prepare()
                .createObservable();

        getStub.verifyQueryBehaviorForList(testItemsObservable);
    }

    @Test
    public void getListOfObjectsObservableStreamTakeOne() {
        final GetStub getStub = new GetStub();

        final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolverForObject)
                .prepare()
                .createObservableStream()
                .take(1);

        getStub.verifyQueryBehaviorForList(testItemsObservable);

        verify(getStub.storIOSQLite, times(1)).observeChangesInTables(eq(Collections.singleton(getStub.query.table)));
    }

    @Test
    public void getCursorWithRawQueryBlocking() {
        final GetStub getStub = new GetStub();

        final Cursor cursor = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehaviorForCursor(cursor);
    }

    @Test
    public void getCursorWithRawQueryObservable() {
        final GetStub getStub = new GetStub();

        final Observable<Cursor> cursorObservable = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .createObservable();

        getStub.verifyRawQueryBehaviorForCursor(cursorObservable);
    }

    @Test
    public void getCursorWithRawQueryObservableStreamTakeOne() {
        final GetStub getStub = new GetStub();

        final Observable<Cursor> cursorObservable = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .createObservableStream()
                .take(1);

        getStub.verifyRawQueryBehaviorForCursor(cursorObservable);

        assertNotNull(getStub.rawQuery.affectedTables);

        verify(getStub.storIOSQLite, times(1)).observeChangesInTables(getStub.rawQuery.affectedTables);
    }

    @Test
    public void getListOfObjectsWithRawQueryBlocking() {
        final GetStub getStub = new GetStub();

        final List<TestItem> testItems = getStub.storIOSQLite
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForObject)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehaviorForList(testItems);
    }

    @Test
    public void getListOfObjectsWithRawQueryObservable() {
        final GetStub getStub = new GetStub();

        final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForObject)
                .prepare()
                .createObservable();

        getStub.verifyRawQueryBehaviorForList(testItemsObservable);
    }

    @Test
    public void getListOfObjectsWithRawQueryObservableStreamTakeOne() {
        final GetStub getStub = new GetStub();

        final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForObject)
                .prepare()
                .createObservableStream()
                .take(1);

        getStub.verifyRawQueryBehaviorForList(testItemsObservable);

        assertNotNull(getStub.rawQuery.affectedTables);

        verify(getStub.storIOSQLite, times(1)).observeChangesInTables(getStub.rawQuery.affectedTables);
    }

}
