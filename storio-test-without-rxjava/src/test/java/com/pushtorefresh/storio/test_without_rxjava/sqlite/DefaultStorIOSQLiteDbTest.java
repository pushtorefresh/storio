package com.pushtorefresh.storio.test_without_rxjava.sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLiteDb;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DefaultStorIOSQLiteDbTest {

    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        new DefaultStorIOSQLiteDb.Builder()
                .db(mock(SQLiteDatabase.class))
                .build();
    }
}
