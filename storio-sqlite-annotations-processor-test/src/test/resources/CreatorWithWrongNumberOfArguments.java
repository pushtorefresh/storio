package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class CreatorWithWrongNumberOfArguments {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id();

    @StorIOSQLiteCreator
    static CreatorWithWrongNumberOfArguments creator();
}
