package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class NonStaticCreatorMethod {

    @StorIOSQLiteCreator
    NonStaticCreatorMethod creator() {
        return new NonStaticCreatorMethod();
    }
}