package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class NonStaticCreatorMethod {

    @StorIOContentResolverCreator
    NonStaticCreatorMethod creator() {
        return new NonStaticCreatorMethod();
    }
}