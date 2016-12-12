package com.pushtorefresh.storio.basic_sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(@NonNull Context context, int version) {
        super(context, "sample_db", null, version);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        TweetTable.createTable(db);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        TweetTable.updateTable(db, oldVersion);
    }
}
