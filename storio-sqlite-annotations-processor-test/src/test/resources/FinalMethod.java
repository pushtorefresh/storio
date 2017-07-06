package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class FinalMethod {

    private long id;

    @StorIOSQLiteColumn(name = "id", key = true)
    final long id() {
        return id;
    }

    @StorIOSQLiteCreator
    public FinalMethod(long id) {
        this.id = id;
    }
}