package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class NoKey {

    @StorIOContentResolverColumn(name = "id")
    long id;
}