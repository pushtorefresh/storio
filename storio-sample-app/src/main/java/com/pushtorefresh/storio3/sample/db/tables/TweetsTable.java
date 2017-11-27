package com.pushtorefresh.storio3.sample.db.tables;

import android.support.annotation.NonNull;

// We suggest to store table meta such as table name, columns names, queries, etc in separate class
// Because it makes code of the Entity itself cleaner and easier to read/understand/support
public class TweetsTable {

    @NonNull
    public static final String TABLE = "tweets";

    @NonNull
    public static final String COLUMN_ID = "_id";

    /**
     * For example: "artem_zin" without "@"
     */
    @NonNull
    public static final String COLUMN_AUTHOR = "author";

    /**
     * For example: "Check out StorIO — modern API for SQLiteDatabase & ContentResolver #androiddev"
     */
    @NonNull
    public static final String COLUMN_CONTENT = "content";

    public static final String COLUMN_ID_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_ID;
    public static final String COLUMN_AUTHOR_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_AUTHOR;
    public static final String COLUMN_CONTENT_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_CONTENT;

    // This is just class with Meta Data, we don't need instances
    private TweetsTable() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_AUTHOR + " TEXT NOT NULL, "
                + COLUMN_CONTENT + " TEXT NOT NULL"
                + ");";
    }
}
