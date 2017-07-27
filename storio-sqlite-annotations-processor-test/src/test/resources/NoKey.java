package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class NoKey {

    @StorIOSQLiteColumn(name = "id")
    long id;
}