package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateMethod {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id() {
        return 0;
    }
}
