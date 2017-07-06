package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class NoCreator {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id() {
        return 0;
    }
}