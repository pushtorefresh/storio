package com.pushtorefresh.storio.sqlite.impl;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
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

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void addTypeDefinitionNullType() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .addDefaultsForType(null, mock(SQLiteTypeDefaults.class))
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void addTypeDefinitionNullDefinition() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .addDefaultsForType(Object.class, null)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void typeDefinition() {
        class TestItem {

        }

        final SQLiteTypeDefaults<TestItem> testItemTypeDefinition = mock(SQLiteTypeDefaults.class);

        final StorIOSQLite storIOSQLite = new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .addDefaultsForType(TestItem.class, testItemTypeDefinition)
                .build();

        assertEquals(testItemTypeDefinition, storIOSQLite.internal().typeDefaults(TestItem.class));
    }
}
