package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithoutSetter {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;

    public long getId() {
        return id;
    }
}