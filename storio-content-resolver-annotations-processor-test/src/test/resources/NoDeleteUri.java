package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(insertUri = "content://insertUri",
        updateUri = "content://updateUri")
public class NoDeleteUri {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;
}