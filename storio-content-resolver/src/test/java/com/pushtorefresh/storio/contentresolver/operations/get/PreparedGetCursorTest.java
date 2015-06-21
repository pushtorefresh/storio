package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;

import org.junit.Test;

import rx.Observable;

public class PreparedGetCursorTest {

    @Test
    public void getCursorBlocking() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Cursor cursor = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForCursor(cursor);
    }


    @Test
    public void getCursorObservable() {
        final GetCursorStub getStub = GetCursorStub.newInstance();

        final Observable<Cursor> cursorObservable = getStub.storIOContentResolver
                .get()
                .cursor()
                .withQuery(getStub.query)
                .withGetResolver(getStub.getResolver)
                .prepare()
                .createObservable()
                .take(1);

        getStub.verifyQueryBehaviorForCursor(cursorObservable);
    }
}
