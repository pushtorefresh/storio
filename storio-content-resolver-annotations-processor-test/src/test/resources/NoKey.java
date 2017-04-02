package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class NoKey {

    @StorIOContentResolverColumn(name = "id")
    long id;
}