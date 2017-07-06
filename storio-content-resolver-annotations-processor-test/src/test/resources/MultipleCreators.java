package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class MultipleCreators {

    @StorIOContentResolverCreator
    static MultipleCreators creator1() {
        return new MultipleCreators();
    }

    @StorIOContentResolverCreator
    static MultipleCreators creator2() {
        return new MultipleCreators();
    }
}