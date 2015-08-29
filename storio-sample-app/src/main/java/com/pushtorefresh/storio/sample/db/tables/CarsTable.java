package com.pushtorefresh.storio.sample.db.tables;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.Query;

// We suggest to store table meta such as table name, columns names, queries, etc in separate class
// Because it makes code of the Entity itself cleaner and easier to read/understand/support
public class CarsTable {

    @NonNull
    public static final String TABLE = "cars";

    @NonNull
    public static final String COLUMN_ID = "_id";

    /**
     * reference to persons table -> foreign key
     */
    @NonNull
    public static final String COLUMN_ID_PERSON = "id_person";

    @NonNull
    public static final String COLUMN_MODEL = "model";

    // Yep, with StorIO you can safely store queries as objects and reuse them, they are immutable
    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    // This is just class with Meta Data, we don't need instances
    private CarsTable() {
        throw new IllegalStateException("No instances please");
    }

    // Better than static final field -> allows VM to unload useless String
    // Because you need this string only once per application life on the device
    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_ID_PERSON + " INTEGER NOT NULL, "
                + COLUMN_MODEL + " TEXT NOT NULL"
                + ");";
    }
}
