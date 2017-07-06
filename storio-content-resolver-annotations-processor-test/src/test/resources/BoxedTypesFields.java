package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class BoxedTypesFields {

    @StorIOContentResolverColumn(name = "field1")
    Boolean field1;

    @StorIOContentResolverColumn(name = "field2")
    Short field2;

    @StorIOContentResolverColumn(name = "field3")
    Integer field3;

    @StorIOContentResolverColumn(name = "field4", key = true)
    Long field4;

    @StorIOContentResolverColumn(name = "field5")
    Float field5;

    @StorIOContentResolverColumn(name = "field6")
    Double field6;
}