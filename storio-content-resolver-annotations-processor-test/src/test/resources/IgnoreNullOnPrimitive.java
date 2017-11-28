package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class IgnoreNullOnPrimitive {

    @StorIOContentResolverColumn(name = "id", key = true, ignoreNull = true)
    long id;
}