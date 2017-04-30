package com.pushtorefresh.storio.sample.db.tables;

import android.support.annotation.NonNull;

public final class PersonsTable {

    private PersonsTable() {
        throw new IllegalStateException("No instances please!");
    }

    @NonNull
    public static final String TABLE_NAME = "persons";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_NAME = "name";

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL"
                + ");";
    }
}
