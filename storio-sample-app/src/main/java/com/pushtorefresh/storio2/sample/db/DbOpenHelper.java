package com.pushtorefresh.storio2.sample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sample.db.tables.TweetsTable;
import com.pushtorefresh.storio2.sample.db.tables.UsersTable;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.CarTable;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonCarRelationTable;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonTable;
import com.pushtorefresh.storio2.sample.sqldelight.entities.Customer;

public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(@NonNull Context context) {
        super(context, "sample_db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(TweetsTable.getCreateTableQuery());
        db.execSQL(UsersTable.getCreateTableQuery());

        CarTable.createTable(db);
        PersonTable.createTable(db);
        db.execSQL(PersonCarRelationTable.getCreateTableQuery());

        db.execSQL(Customer.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // no impl
    }
}
