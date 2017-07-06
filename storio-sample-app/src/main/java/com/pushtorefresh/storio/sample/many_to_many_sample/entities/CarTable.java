package com.pushtorefresh.storio.sample.many_to_many_sample.entities;

import android.support.annotation.NonNull;

public class CarTable {

    @NonNull
    public static final String TABLE = "cars";

    @NonNull
    public static final String COLUMN_ID = "_car_id";

    @NonNull
    public static final String COLUMN_MODEL = "model";

    // This is just class with Meta Data, we don't need instances
    private CarTable() {
        throw new IllegalStateException("No instances please");
    }

    // Better than static final field -> allows VM to unload useless String
    // Because you need this string only once per application life on the device
    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_MODEL + " TEXT NOT NULL"
                + ");";
    }
}
