package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class NoKey {

    @StorIOSQLiteColumn(name = "id")
    long id;
}