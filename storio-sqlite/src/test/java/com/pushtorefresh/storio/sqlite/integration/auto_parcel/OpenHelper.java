package com.pushtorefresh.storio.sqlite.integration.auto_parcel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

class OpenHelper extends SQLiteOpenHelper {

    OpenHelper(@NonNull Context context) {
        super(context, "auto_parcel_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BookTableMeta.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
