package com.pushtorefresh.storio.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio.sample.db.tables.UsersTable;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.CarTable;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.PersonCarRelationTable;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.PersonTable;

public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(@NonNull Context context) {
        super(context, "sample_db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(TweetsTable.getCreateTableQuery());
        db.execSQL(UsersTable.getCreateTableQuery());

        db.execSQL(CarTable.getCreateTableQuery());
        db.execSQL(PersonTable.getCreateTableQuery());
        db.execSQL(PersonCarRelationTable.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // no impl
    }
}
