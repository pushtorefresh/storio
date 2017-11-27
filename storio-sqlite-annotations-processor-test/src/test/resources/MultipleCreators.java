package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class MultipleCreators {

    @StorIOSQLiteCreator
    static MultipleCreators creator1() {
        return new MultipleCreators();
    }

    @StorIOSQLiteCreator
    static MultipleCreators creator2() {
        return new MultipleCreators();
    }
}