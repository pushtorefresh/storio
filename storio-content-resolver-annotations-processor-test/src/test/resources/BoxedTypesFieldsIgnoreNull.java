package com.pushtorefresh.storio.contentresolver.annotations;

@StorIOContentResolverType(uri = "content://uri")
public class BoxedTypesFieldsIgnoreNull {

    @StorIOContentResolverColumn(name = "field1", ignoreNull = true)
    Boolean field1;

    @StorIOContentResolverColumn(name = "field2", ignoreNull = true)
    Short field2;

    @StorIOContentResolverColumn(name = "field3", ignoreNull = true)
    Integer field3;

    @StorIOContentResolverColumn(name = "field4", key = true, ignoreNull = true)
    Long field4;

    @StorIOContentResolverColumn(name = "field5", ignoreNull = true)
    Float field5;

    @StorIOContentResolverColumn(name = "field6", ignoreNull = true)
    Double field6;
}