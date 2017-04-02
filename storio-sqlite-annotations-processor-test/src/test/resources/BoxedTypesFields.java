package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class BoxedTypesFields {

    @StorIOSQLiteColumn(name = "field1")
    Boolean field1;

    @StorIOSQLiteColumn(name = "field2")
    Short field2;

    @StorIOSQLiteColumn(name = "field3")
    Integer field3;

    @StorIOSQLiteColumn(name = "field4", key = true)
    Long field4;

    @StorIOSQLiteColumn(name = "field5")
    Float field5;

    @StorIOSQLiteColumn(name = "field6")
    Double field6;
}