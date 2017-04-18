package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateFieldWithoutSetter {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id;

    public long getId() {
        return id;
    }
}