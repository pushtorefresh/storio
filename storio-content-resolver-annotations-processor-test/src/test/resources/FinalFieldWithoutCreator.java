package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class FinalFieldWithoutCreator {

    @StorIOContentResolverColumn(name = "id", key = true)
    final long id;
}