package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class AbsenceOfNoArgConstructor {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;

    public AbsenceOfNoArgConstructor(long id) {
        this.id = id;
    }
}