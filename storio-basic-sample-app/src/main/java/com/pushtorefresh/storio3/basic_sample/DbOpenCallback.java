package com.pushtorefresh.storio3.basic_sample;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

public class DbOpenCallback extends SupportSQLiteOpenHelper.Callback {

    public static final String DB_NAME = "sample_db";

    public DbOpenCallback() {
        super(1);
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        TweetTable.createTable(db);
    }

    @Override
    public void onUpgrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {
        TweetTable.updateTable(db, oldVersion);
    }
}
