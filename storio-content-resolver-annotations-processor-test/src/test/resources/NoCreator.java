package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class NoCreator {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id() {
        return 0;
    }
}