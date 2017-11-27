package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class EmptyColumnName {

    @StorIOContentResolverColumn(name = "", key = true)
    long id;
}