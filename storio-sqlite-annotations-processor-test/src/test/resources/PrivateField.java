package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateField {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;
}
