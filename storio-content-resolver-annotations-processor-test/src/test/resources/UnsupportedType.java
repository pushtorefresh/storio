package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class UnsupportedType {

    @StorIOContentResolverColumn(name = "id", key = true)
    long id;

    @StorIOContentResolverColumn(name = "class")
    UnsupprtedClass unsupprtedClass;

    public class UnsupprtedClass {

    }
}