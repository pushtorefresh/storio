package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateCreator {

    @StorIOContentResolverCreator
    private PrivateCreator() {
        return new PrivateCreator();
    }
}