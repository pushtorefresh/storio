package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateField {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id;
}
