package com.pushtorefresh.storio.contentresolver.integration;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class IntegrationSQLiteOpenHelper extends SQLiteOpenHelper {

    static final String TABLE_TEST_ITEMS = "test_items";

    public IntegrationSQLiteOpenHelper(@NonNull Context context) {
        super(context, "integration_db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_TEST_ITEMS + "("
                + TestItem.COLUMN_ID + " INTEGER PRIMARY KEY, "
                + TestItem.COLUMN_VALUE + " TEXT NOT NULL, "
                + TestItem.COLUMN_OPTIONAL_VALUE + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new AssertionError("Must not be called");
    }
}
