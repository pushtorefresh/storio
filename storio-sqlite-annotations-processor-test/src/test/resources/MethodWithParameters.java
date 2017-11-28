package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class MethodWithParameters {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id(long id) {
        return id;
    }
}