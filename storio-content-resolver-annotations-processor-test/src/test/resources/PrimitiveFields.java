package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class PrimitiveFields {

    @StorIOContentResolverColumn(name = "field1")
    boolean field1;

    @StorIOContentResolverColumn(name = "field2")
    short field2;

    @StorIOContentResolverColumn(name = "field3")
    int field3;

    @StorIOContentResolverColumn(name = "field4", key = true)
    long field4;

    @StorIOContentResolverColumn(name = "field5")
    float field5;

    @StorIOContentResolverColumn(name = "field6")
    double field6;

    @StorIOContentResolverColumn(name = "field7")
    String field7;

    @StorIOContentResolverColumn(name = "field8")
    byte[] field8;
}