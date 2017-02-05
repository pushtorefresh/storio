package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrimitiveFields {

    @StorIOSQLiteColumn(name = "booleanField")
    boolean booleanField;

    @StorIOSQLiteColumn(name = "shortField")
    short shortField;

    @StorIOSQLiteColumn(name = "intField")
    int intField;

    @StorIOSQLiteColumn(name = "longField", key = true)
    long longField;

    @StorIOSQLiteColumn(name = "floatField")
    float floatField;

    @StorIOSQLiteColumn(name = "doubleField")
    double doubleField;

    @StorIOSQLiteColumn(name = "stringField")
    String stringField;

    @StorIOSQLiteColumn(name = "byteArrayField")
    byte[] byteArrayField;
}
