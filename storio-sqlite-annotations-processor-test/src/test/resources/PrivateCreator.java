package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateCreator {

    @StorIOSQLiteCreator
    private PrivateCreator() {
        return new PrivateCreator();
    }
}