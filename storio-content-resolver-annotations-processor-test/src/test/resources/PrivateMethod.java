package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateMethod {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id() {
        return 0;
    }
}