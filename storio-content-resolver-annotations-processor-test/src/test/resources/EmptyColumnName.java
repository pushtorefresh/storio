package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class EmptyColumnName {

    @StorIOContentResolverColumn(name = "", key = true)
    long id;
}