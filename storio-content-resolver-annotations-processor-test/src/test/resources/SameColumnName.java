package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class SameColumnName {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;

    @StorIOContentResolverColumn(name = "id")
    String name;
}