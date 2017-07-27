package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class SameColumnName {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;

    @StorIOSQLiteColumn(name = "id")
    String name;
}