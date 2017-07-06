package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateFieldWithIsGetter {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id;

    @StorIOContentResolverColumn(name = "flag")
    private boolean flag;

    @StorIOContentResolverCreator
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