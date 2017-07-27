package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class MethodWithParameters {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id(long id) {
        return id;
    }
}