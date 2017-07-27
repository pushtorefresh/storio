package com.pushtorefresh.storio2.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class CreatorWithWrongNumberOfArguments {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id() {
        return 0;
    }

    @StorIOContentResolverCreator
    static CreatorWithWrongNumberOfArguments creator(long id, String some) {
        return new CreatorWithWrongNumberOfArguments();
    }
}