package com.pushtorefresh.storio2.sample.many_to_many_sample.entities;

import android.support.annotation.NonNull;

public class PersonCarRelationTable {

    @NonNull
    public static final String TABLE = "persons_to_cars";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_PERSON_ID = "person_id";

    @NonNull
    public static final String COLUMN_CAR_ID = "car_id";

    // This is just class with Meta Data, we don't need instances
    private PersonCarRelationTable() {
        throw new IllegalStateException("No instances please");
    }

    // Better than static final field -> allows VM to unload useless String
    // Because you need this string only once per application life on the device
    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_PERSON_ID + " INTEGER NOT NULL,"
                + COLUMN_CAR_ID + " INTEGER NOT NULL"
                + ");";
    }
}
