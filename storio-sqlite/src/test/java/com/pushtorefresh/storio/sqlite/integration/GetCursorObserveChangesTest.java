package com.pushtorefresh.storio.sqlite.integration;


import android.database.Cursor;

import org.junit.Test;

import rx.observers.TestSubscriber;

public class GetCursorObserveChangesTest extends BaseOperationObserveChangesTest {

    @Test
    public void repeatsOperationWithQueryByChangeOfTable() {
        putUserBlocking();

        TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();
        storIOSQLite
                .get()
                .cursor()
                .withQuery(query)
                .prepare()
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);

        storIOSQLite.lowLevel().notifyAboutChanges(tableChanges);

        testSubscriber.assertValueCount(2);
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTable() {
        putUserBlocking();

        TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();
        storIOSQLite
                .get()
                .cursor()
                .withQuery(rawQuery)
                .prepare()
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);

        storIOSQLite.lowLevel().notifyAboutChanges(tableChanges);

        testSubscriber.assertValueCount(2);
    }

    @Test
    public void repeatsOperationWithQueryByChangeOfTag() {
        putUserBlocking();

        TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();
        storIOSQLite
                .get()
                .cursor()
                .withQuery(query)
                .prepare()
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);

        storIOSQLite.lowLevel().notifyAboutChanges(tagChanges);

        testSubscriber.assertValueCount(2);
    }

    @Test
    public void repeatsOperationWithRawQueryByChangeOfTag() {
        putUserBlocking();

        TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();
        storIOSQLite
                .get()
                .cursor()
                .withQuery(rawQuery)
                .prepare()
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);

        storIOSQLite.lowLevel().notifyAboutChanges(tagChanges);

        testSubscriber.assertValueCount(2);
    }
}
