package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class FinalFieldWithoutCreator {

    @StorIOSQLiteColumn(name = "id", key = true)
    final long id;
}