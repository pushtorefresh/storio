package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class EmptyColumnName {

    @StorIOSQLiteColumn(name = "", key = true)
    long id;
}