package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithoutGetter {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;
}