package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class NoKey {

    @StorIOSQLiteColumn(name = "id")
    long id;
}