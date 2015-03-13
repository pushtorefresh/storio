package com.pushtorefresh.storio.db.integration_test.impl;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.impl.StorIOSQLiteDb;

import org.junit.Before;

public abstract class BaseTest {

    @NonNull protected StorIODb storIODb;
    @NonNull protected SQLiteDatabase db;

    @Before public void setUp() throws Exception {
        db = new TestSQLiteOpenHelper(InstrumentationRegistry.getContext())
                .getWritableDatabase();

        storIODb = new StorIOSQLiteDb.Builder()
                .db(db)
                .build();

        // clearing db before each test case
        storIODb
                .delete()
                .byQuery(User.DELETE_ALL)
                .prepare()
                .executeAsBlocking();
    }
}
