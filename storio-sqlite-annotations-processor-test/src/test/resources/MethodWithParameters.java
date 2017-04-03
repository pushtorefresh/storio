package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class MethodWithParameters {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id(long id) {
        return id;
    }
}