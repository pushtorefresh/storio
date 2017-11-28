package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithCorrespondingAccessors {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
