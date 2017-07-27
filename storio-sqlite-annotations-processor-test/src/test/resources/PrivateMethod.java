package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateMethod {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id() {
        return 0;
    }
}
