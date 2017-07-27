package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class CreatorWithNotMatchingArguments {

    private long id;

    @StorIOSQLiteColumn(name = "id", key = true)
    long id() {
        return id;
    }

    @StorIOSQLiteCreator
    public CreatorWithNotMatchingArguments(long notMatchingName) {
        this.id = notMatchingName;
    }
}