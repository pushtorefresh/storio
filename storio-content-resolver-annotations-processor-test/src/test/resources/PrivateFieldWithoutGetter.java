package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateFieldWithoutGetter {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id;

    public void setId(long id) {
        this.id = id;
    }
}