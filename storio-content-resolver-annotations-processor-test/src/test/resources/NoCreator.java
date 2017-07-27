package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class NoCreator {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id() {
        return 0;
    }
}