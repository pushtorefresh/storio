package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType()
public class NoUris {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}