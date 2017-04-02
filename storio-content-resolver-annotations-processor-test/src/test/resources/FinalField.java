package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class FinalField {

    @StorIOContentResolverColumn(name = "id", key = true)
    final long id;
}