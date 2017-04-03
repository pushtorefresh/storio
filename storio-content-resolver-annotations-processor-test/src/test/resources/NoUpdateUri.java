package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(insertUri = "content://insertUri",
        deleteUri = "content://deleteUri")
public class NoUpdateUri {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}