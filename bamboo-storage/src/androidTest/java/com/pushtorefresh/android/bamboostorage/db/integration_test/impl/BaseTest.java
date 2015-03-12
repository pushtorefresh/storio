package com.pushtorefresh.android.bamboostorage.db.integration_test.impl;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.pushtorefresh.android.bamboostorage.db.BambooStorageDb;
import com.pushtorefresh.android.bamboostorage.db.impl.BambooStorageSQLiteDb;

import org.junit.Before;

public abstract class BaseTest {

    @NonNull protected BambooStorageDb bambooStorageDb;
    @NonNull protected SQLiteDatabase db;

    @Before public void setUp() throws Exception {
        db = new TestSQLiteOpenHelper(InstrumentationRegistry.getContext())
                .getWritableDatabase();

        bambooStorageDb = new BambooStorageSQLiteDb.Builder()
                .db(db)
                .build();

        // clearing db before each test case
        bambooStorageDb
                .delete()
                .byQuery(User.DELETE_ALL)
                .prepare()
                .executeAsBlocking();
    }
}
