package com.pushtorefresh.storio3.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class AbsenceOfNoArgConstructor {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;

    public AbsenceOfNoArgConstructor(long id) {
        this.id = id;
    }
}