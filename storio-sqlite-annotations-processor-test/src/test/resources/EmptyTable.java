package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "")
public class EmptyTable {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;
}