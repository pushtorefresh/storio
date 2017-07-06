package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateMethod {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id() {
        return 0;
    }
}
