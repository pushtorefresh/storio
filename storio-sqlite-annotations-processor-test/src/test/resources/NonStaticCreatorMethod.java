package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class NonStaticCreatorMethod {

    @StorIOSQLiteCreator
    NonStaticCreatorMethod creator() {
        return new NonStaticCreatorMethod();
    }
}