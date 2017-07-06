package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class CreatorWithWrongNumberOfArguments {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id() {
        return 0;
    }

    @StorIOSQLiteCreator
    static CreatorWithWrongNumberOfArguments creator(long id, String some) {
        return new CreatorWithWrongNumberOfArguments();
    }
}