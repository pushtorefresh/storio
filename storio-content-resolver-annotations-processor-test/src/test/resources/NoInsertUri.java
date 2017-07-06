package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(deleteUri = "content://deleteUri",
        updateUri = "content://updateUri")
public class NoInsertUri {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}