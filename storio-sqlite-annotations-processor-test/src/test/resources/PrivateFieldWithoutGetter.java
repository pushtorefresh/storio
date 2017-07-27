package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithoutGetter {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;
}