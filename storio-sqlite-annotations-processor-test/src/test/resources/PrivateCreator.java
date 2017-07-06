package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateCreator {

    @StorIOSQLiteCreator
    private PrivateCreator() {
        return new PrivateCreator();
    }
}