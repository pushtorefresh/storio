package com.pushtorefresh.android.bamboostorage.integration_test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.impl.BambooStorageFromDB;

public class TestDBBambooStorageFactory {

    @NonNull private static SQLiteOpenHelper getTestSQLiteOpenHelper(@NonNull Context context) {
        return new SQLiteOpenHelper(context, "integration_test_db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(User.CREATE_TABLE_QUERY);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }

    @NonNull public static BambooStorage getTestBambooStorageFromDB(@NonNull Context context) {
        return new BambooStorageFromDB(getTestSQLiteOpenHelper(context));
    }
}
