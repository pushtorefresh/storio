package com.pushtorefresh.storio.sample.db.table;

import com.pushtorefresh.storio.sqlite.query.Query;

public abstract class TweetTableMeta {

    public static final String TABLE = "tweets";
    public static final String COLUMN_ID = "_id";
    /**
     * For example: "artem_zin" without "@"
     */
    public static final String COLUMN_AUTHOR = "author";
    /**
     * For example: "Check out StorIO — modern API for SQLiteDatabase & ContentResolver #androiddev"
     */
    public static final String COLUMN_CONTENT = "content";

    public static final Query QUERY_ALL = new Query.Builder()
            .table(TABLE)
            .build();
}
