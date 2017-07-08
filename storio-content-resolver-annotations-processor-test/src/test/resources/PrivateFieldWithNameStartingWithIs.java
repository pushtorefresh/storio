package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateFieldWithNameStartingWithIs {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id;

    @StorIOContentResolverColumn(name = "is_flag")
    private boolean isFlag;

    @StorIOContentResolverCreator
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