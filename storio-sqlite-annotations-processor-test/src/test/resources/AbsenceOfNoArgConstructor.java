package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class AbsenceOfNoArgConstructor {

    @StorIOSQLiteColumn(name = "id", key = true)
    long id;

    public AbsenceOfNoArgConstructor(long id) {
        this.id = id;
    }
}