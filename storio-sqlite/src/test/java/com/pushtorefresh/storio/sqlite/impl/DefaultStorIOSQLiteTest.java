package com.pushtorefresh.storio.sqlite.impl;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultStorIOSQLiteTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullSQLiteDb() {
        new DefaultStorIOSQLite.Builder()
                .db(null)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullSQLiteOpenHelper() {
        new DefaultStorIOSQLite.Builder()
                .sqliteOpenHelper(null)
                .build();
    }

    @Test
    public void buildSQLiteOpenHelper() {
        final SQLiteOpenHelper sqLiteOpenHelper = mock(SQLiteOpenHelper.class);

        when(sqLiteOpenHelper.getWritableDatabase())
                .thenReturn(mock(SQLiteDatabase.class));

        new DefaultStorIOSQLite.Builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .build();

        verify(sqLiteOpenHelper, times(1)).getWritableDatabase();
    }
}
