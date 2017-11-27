package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithNameStartingWithIs {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;

    @StorIOSQLiteColumn(name = "is_flag")
    private boolean isFlag;

    @StorIOSQLiteCreator
    public PrivateFieldWithNameStartingWithIs(long id, boolean isFlag) {
        this.id = id;
        this.isFlag = isFlag;
    }

    public long getId() {
        return id;
    }

    public boolean isFlag() {
        return isFlag;
    }
}