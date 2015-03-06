package com.pushtorefresh.android.bamboostorage.integration_test.impl;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.impl.BSSQLiteDatabase;

import org.junit.Before;

public abstract class BaseTest {

    @NonNull protected BambooStorage bambooStorage;
    @NonNull protected SQLiteDatabase db;

    @Before public void setUp() throws Exception {
        db = new TestSQLiteOpenHelper(InstrumentationRegistry.getContext())
                .getWritableDatabase();

        bambooStorage = new BSSQLiteDatabase.Builder()
                .db(db)
                .build();

        // clearing db before each test case
        bambooStorage
                .delete()
                .byQuery(User.DELETE_ALL)
                .prepare()
                .executeAsBlocking();
    }
}
