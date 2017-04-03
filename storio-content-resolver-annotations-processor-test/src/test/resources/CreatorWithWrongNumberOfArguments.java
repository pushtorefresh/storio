package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class CreatorWithWrongNumberOfArguments {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id() {
        return 0;
    }

    @StorIOContentResolverCreator
    static CreatorWithWrongNumberOfArguments creator() {
        return new CreatorWithWrongNumberOfArguments();
    }
}