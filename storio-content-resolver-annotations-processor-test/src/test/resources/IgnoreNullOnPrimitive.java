package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class IgnoreNullOnPrimitive {

    @StorIOContentResolverColumn(name = "id", key = true, ignoreNull = true)
    long id;
}