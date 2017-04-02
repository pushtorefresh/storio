package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class IgnoreNullOnPrimitive {

    @StorIOSQLiteColumn(name = "id", key = true, ignoreNull = true)
    long id;
}