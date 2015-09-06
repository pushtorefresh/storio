package com.pushtorefresh.storio.sample.db.tables;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.Query;

public class AntsTable {

    @NonNull
    public static final String TABLE = "ants";

    @NonNull
    public static final String COLUMN_ID = "_id";

    /**
     * reference to queens table -> foreign key
     */
    @NonNull
    public static final String COLUMN_ID_QUEEN = "id_queen";

    @NonNull
    public static final String COLUMN_NAME = "name";

    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    // This is just class with Meta Data, we don't need instances
    private AntsTable() {
        throw new IllegalStateException("No instances please");
    }

    // Better than static final field -> allows VM to unload useless String
    // Because you need this string only once per application life on the device
    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID         + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_ID_QUEEN   + " INTEGER NOT NULL, "
                + COLUMN_NAME       + " TEXT NOT NULL"
                + ");";
    }
}
