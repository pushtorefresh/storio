package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithIsGetter {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;

    @StorIOSQLiteColumn(name = "flag")
    private boolean flag;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}