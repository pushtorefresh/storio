package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class BoxedTypesFields {

    @StorIOSQLiteColumn(name = "booleanField")
    Boolean booleanField;

    @StorIOSQLiteColumn(name = "shortField")
    Short shortField;

    @StorIOSQLiteColumn(name = "intField")
    Integer intField;

    @StorIOSQLiteColumn(name = "longField", key = true)
    Long longField;

    @StorIOSQLiteColumn(name = "floatField")
    Float floatField;

    @StorIOSQLiteColumn(name = "doubleField")
    Double doubleField;
}
