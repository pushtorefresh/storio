package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "")
public class EmptyTable {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;
}