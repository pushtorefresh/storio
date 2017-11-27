package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFinalFieldWithGetterAndCreator {

    @StorIOSQLiteColumn(name = "id", key = true)
    private final long id;

    @StorIOSQLiteCreator
    public PrivateFinalFieldWithGetterAndCreator(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}