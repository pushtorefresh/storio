package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class CreatorWithNotMatchingArguments {

    private long id;

    @StorIOContentResolverColumn(name = "id", key = true)
    long id() {
        return id;
    }

    @StorIOContentResolverCreator
    public CreatorWithNotMatchingArguments(long it) {
        this.id = it;
    }
}