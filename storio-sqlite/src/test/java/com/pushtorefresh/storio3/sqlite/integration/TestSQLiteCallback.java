package com.pushtorefresh.storio3.sqlite.integration;

import android.support.annotation.NonNull;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

class TestSQLiteCallback extends SupportSQLiteOpenHelper.Callback {

    public static final String DB_NAME = "test_db";

    TestSQLiteCallback() {
        super(1);
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL(UserTableMeta.SQL_CREATE_TABLE);
        db.execSQL(TweetTableMeta.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
