package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class AbsenceOfNoArgConstructor {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;

    public AbsenceOfNoArgConstructor(long id) {
        this.id = id;
    }
}