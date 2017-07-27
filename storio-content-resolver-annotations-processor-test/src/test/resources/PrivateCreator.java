package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrivateCreator {

    @StorIOContentResolverCreator
    private PrivateCreator() {
        return new PrivateCreator();
    }
}