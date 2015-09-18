package com.pushtorefresh.storio.sample.db.tables;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.InsertQuery;

public final class CarsTable {

    private CarsTable() {
        throw new IllegalStateException("No instances please!");
    }

    @NonNull
    public static final String TABLE_NAME = "cars";

//    @NonNull
//    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_UUID = "uuid";

//    @NonNull
//    public static final String COLUMN_PERSON_ID = "person_id";

    @NonNull
    public static final String COLUMN_PERSON_UUID = "person_uuid";

    @NonNull
    public static final String COLUMN_MODEL = "model";

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE_NAME + "("
//                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_UUID + " TEXT NOT NULL PRIMARY KEY, "
//                + COLUMN_PERSON_ID + " INTEGER NOT NULL, " // Can be foreign key if you want
                + COLUMN_PERSON_UUID + " TEXT, " // Can be foreign key if you want
                + COLUMN_MODEL + " TEXT NOT NULL"
                + ");";
    }

    public static final InsertQuery INSERT_QUERY_CAR = InsertQuery.builder().table(CarsTable.TABLE_NAME).build();

}