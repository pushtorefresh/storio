package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class FinalMethod {

    private long id;

    @StorIOContentResolverColumn(name = "id", key = true)
    final long id() {
        return id;
    }

    @StorIOContentResolverCreator
    public FinalMethod(long id) {
        this.id = id;
    }
}