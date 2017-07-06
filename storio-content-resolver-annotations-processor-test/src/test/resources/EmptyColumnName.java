package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class EmptyColumnName {

    @StorIOContentResolverColumn(name = "", key = true)
    long id;
}