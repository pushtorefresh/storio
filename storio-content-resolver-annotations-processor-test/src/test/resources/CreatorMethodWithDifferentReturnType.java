package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class CreatorMethodWithDifferentReturnType {

    @StorIOContentResolverCreator
    static int creator() {
        return 0;
    }
}