package com.pushtorefresh.storio.sample.db.tables;

import android.support.annotation.NonNull;

// We suggest to store table meta such as table name, columns names, queries, etc in separate class
// Because it makes code of the Entity itself cleaner and easier to read/understand/support
public class UsersTable {

    @NonNull
    public static final String TABLE = "users";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_NICK = "nick";

    @NonNull
    public static final String COLUMN_ID_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_ID;

    @NonNull
    public static final String COLUMN_NICK_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_NICK;

    // This is just class with Meta Data, we don't need instances
    private UsersTable() {
        throw new IllegalStateException("No instances please");
    }

    // Better than static final field -> allows VM to unload useless String
    // Because you need this string only once per application life on the device
    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_NICK + " TEXT NOT NULL UNIQUE"
                + ");";
    }
}
