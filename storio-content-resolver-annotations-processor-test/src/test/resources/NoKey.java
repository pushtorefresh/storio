package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class NoKey {

    @StorIOContentResolverColumn(name = "id")
    long id;
}