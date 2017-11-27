package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class NoCreator {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id() {
        return 0;
    }
}