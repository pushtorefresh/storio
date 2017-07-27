package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class CreatorMethodWithDifferentReturnType {

    @StorIOSQLiteCreator
    static int creator() {
        return 0;
    }
}