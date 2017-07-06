package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithoutGetter {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;

    public void setId(long id) {
        this.id = id;
    }
}