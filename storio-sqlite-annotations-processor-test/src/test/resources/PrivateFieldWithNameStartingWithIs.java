package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldWithNameStartingWithIs {

    @StorIOSQLiteColumn(name = "id", key = true)
    private long id;

    @StorIOSQLiteColumn(name = "is_flag")
    private boolean isFlag;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public void setFlag(boolean flag) {
        isFlag = flag;
    }
}