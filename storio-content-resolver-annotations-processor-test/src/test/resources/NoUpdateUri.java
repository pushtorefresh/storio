package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(insertUri = "content://insertUri",
        deleteUri = "content://deleteUri")
public class NoUpdateUri {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}