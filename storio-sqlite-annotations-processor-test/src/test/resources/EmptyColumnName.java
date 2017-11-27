package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class EmptyColumnName {

    @StorIOSQLiteColumn(name = "", key = true)
    long id;
}