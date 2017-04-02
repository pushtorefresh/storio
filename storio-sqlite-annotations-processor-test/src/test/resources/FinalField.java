package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class FinalField {

    @StorIOSQLiteColumn(name = "id", key = true)
    final long id;
}