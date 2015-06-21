package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;

import org.junit.Test;

import rx.Observable;

public class PreparedGetCursorTest {

    @Test
    public void shouldGetCursorWithQueryBlocking() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

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
    public void shouldGetCursorWithQueryAsObservable() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Observable<Cursor> cursorObservable = getStub.storIOSQLite
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
    public void shouldGetCursorWithRawQueryBlocking() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

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
    public void shouldGetCursorWithRawQueryAsObservable() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Observable<Cursor> cursorObservable = getStub.storIOSQLite
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .withGetResolver(getStub.getResolverForCursor)
                .prepare()
                .createObservable()
                .take(1);

        getStub.verifyRawQueryBehaviorForCursor(cursorObservable);
    }
}
