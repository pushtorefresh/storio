package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateFieldWithIsGetter {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id;

    @StorIOContentResolverColumn(name = "flag")
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