package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entity.Tweet;

public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(@NonNull Context context) {
        super(context, "sample_db", null, 1);
    }

    @Override public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(getCreateTweetTableQuery());
    }

    @Override public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // no impl
    }

    // better than static final field -> allows VM to unload useless String
    @NonNull private static String getCreateTweetTableQuery() {
        return "CREATE TABLE " + Tweet.TABLE + "("
                + Tweet.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + Tweet.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + Tweet.COLUMN_CONTENT + " TEXT NOT NULL"
                + ");";
    }
}
