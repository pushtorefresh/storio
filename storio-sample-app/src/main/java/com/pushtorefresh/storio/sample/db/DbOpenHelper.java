package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.table.TweetTableMeta;

public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(@NonNull Context context) {
        super(context, "sample_db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(getCreateTweetTableQuery());
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // no impl
    }

    // better than static final field -> allows VM to unload useless String
    @NonNull
    private static String getCreateTweetTableQuery() {
        return "CREATE TABLE " + TweetTableMeta.TABLE + "("
                + TweetTableMeta.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + TweetTableMeta.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + TweetTableMeta.COLUMN_CONTENT + " TEXT NOT NULL"
                + ");";
    }
}
