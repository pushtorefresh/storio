package com.pushtorefresh.storio3.sqlite.integration.auto_parcel;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

class OpenHelper extends SupportSQLiteOpenHelper.Callback {

    public static final String DB_NAME = "auto_parcel_db";

    OpenHelper() {
        super(1);
    }

    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        db.execSQL(BookTableMeta.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
