package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class MethodWithParameters {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id(long id) {
        return id;
    }
}