package com.pushtorefresh.storio3.sample.db;

import com.pushtorefresh.storio3.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio3.sample.db.tables.UsersTable;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.CarTable;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.PersonCarRelationTable;
import com.pushtorefresh.storio3.sample.many_to_many_sample.entities.PersonTable;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
//import com.pushtorefresh.storio3.sample.sqldelight.entities.Customer;

public class DbOpenCallback extends SupportSQLiteOpenHelper.Callback {

    public static final String DB_NAME = "sample_db";

    public static final int VERSION = 1;

    public DbOpenCallback() {
        super(VERSION);
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL(TweetsTable.getCreateTableQuery());
        db.execSQL(UsersTable.getCreateTableQuery());

        CarTable.createTable(db);
        PersonTable.createTable(db);
        db.execSQL(PersonCarRelationTable.getCreateTableQuery());

//        db.execSQL(Customer.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
