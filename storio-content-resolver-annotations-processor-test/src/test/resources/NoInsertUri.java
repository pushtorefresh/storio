package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(deleteUri = "content://deleteUri",
        updateUri = "content://updateUri")
public class NoInsertUri {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}