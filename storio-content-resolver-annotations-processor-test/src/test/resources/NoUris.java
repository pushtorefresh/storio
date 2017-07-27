package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType()
public class NoUris {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}