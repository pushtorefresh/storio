package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class FinalFieldWithoutCreator {

    @StorIOSQLiteColumn(name = "id", key = true)
    final long id;
}