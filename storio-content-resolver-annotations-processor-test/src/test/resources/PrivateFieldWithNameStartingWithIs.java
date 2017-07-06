package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateFieldWithNameStartingWithIs {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id;

    @StorIOContentResolverColumn(name = "is_flag")
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