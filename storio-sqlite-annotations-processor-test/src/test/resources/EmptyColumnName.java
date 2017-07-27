package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class EmptyColumnName {

    @StorIOSQLiteColumn(name = "", key = true)
    long id;
}