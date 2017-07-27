package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateMethod {

    @StorIOContentResolverColumn(name = "id", key = true)
    private long id() {
        return 0;
    }
}