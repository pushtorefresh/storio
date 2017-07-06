package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class CreatorMethodWithDifferentReturnType {

    @StorIOContentResolverCreator
    static int creator() {
        return 0;
    }
}