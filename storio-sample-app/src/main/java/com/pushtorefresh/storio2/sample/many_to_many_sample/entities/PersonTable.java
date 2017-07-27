package com.pushtorefresh.storio2.sample.many_to_many_sample.entities;

import android.support.annotation.NonNull;

public class PersonTable {

    @NonNull
    public static final String TABLE = "persons";

    @NonNull
    public static final String COLUMN_ID = "_person_id";

    @NonNull
    public static final String COLUMN_NAME = "name";

    // This is just class with Meta Data, we don't need instances
    private PersonTable() {
        throw new IllegalStateException("No instances please");
    }

    // Better than static final field -> allows VM to unload useless String
    // Because you need this string only once per application life on the device
    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL UNIQUE"
                + ");";
    }
}
