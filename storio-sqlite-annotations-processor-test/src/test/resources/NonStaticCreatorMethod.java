package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class NonStaticCreatorMethod {

    @StorIOSQLiteCreator
    NonStaticCreatorMethod creator() {
        return new NonStaticCreatorMethod();
    }
}