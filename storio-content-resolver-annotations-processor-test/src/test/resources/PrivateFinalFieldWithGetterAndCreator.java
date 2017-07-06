package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateFinalFieldWithGetterAndCreator {

    @StorIOContentResolverColumn(name = "id", key = true)
    private final long id;

    @StorIOContentResolverCreator
    public PrivateFinalFieldWithGetterAndCreator(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}