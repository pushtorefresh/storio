package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithIsGetter {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;

    @StorIOSQLiteColumn(name = "flag")
    private boolean flag;

    @StorIOSQLiteCreator
    public PrivateFieldWithIsGetter(long id, boolean flag) {
        this.id = id;
        this.flag = flag;
    }

    public long getId() {
        return id;
    }

    public boolean isFlag() {
        return flag;
    }
}