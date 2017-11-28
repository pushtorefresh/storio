package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType()
public class NoUris {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}