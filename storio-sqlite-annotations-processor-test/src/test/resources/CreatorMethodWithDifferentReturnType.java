package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class CreatorMethodWithDifferentReturnType {

    @StorIOSQLiteCreator
    static int creator();
}
