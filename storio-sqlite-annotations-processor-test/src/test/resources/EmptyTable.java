package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "")
public class EmptyTable {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;
}