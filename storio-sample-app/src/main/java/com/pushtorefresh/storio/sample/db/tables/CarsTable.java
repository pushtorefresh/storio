package com.pushtorefresh.storio.sample.db.tables;

import android.support.annotation.NonNull;

public final class CarsTable {

    private CarsTable() {
        throw new IllegalStateException("No instances please!");
    }

    @NonNull
    public static final String TABLE_NAME = "cars";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_PERSON_ID = "person_id";

    @NonNull
    public static final String COLUMN_MODEL = "model";

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_PERSON_ID + " INTEGER NOT NULL, " // Can be foreign key if you want
                + COLUMN_MODEL + " TEXT NOT NULL"
                + ");";
    }
}
