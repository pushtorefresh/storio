package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class CreatorWithNotMatchingArguments {

    private long id;

    @StorIOContentResolverColumn(name = "id", key = true)
    long id() {
        return id;
    }

    @StorIOContentResolverCreator
    public CreatorWithNotMatchingArguments(long notMatchingName) {
        this.id = notMatchingName;
    }
}