package com.pushtorefresh.storio3.sqlite.annotations;

@StorIOSQLiteType(table = "table", generateTableClass = false)
public class WithoutGeneratedTable {

    @StorIOSQLiteColumn(name = "field1")
    boolean field1;

    @StorIOSQLiteColumn(name = "field2")
    short field2;

    @StorIOSQLiteColumn(name = "field3")
    int field3;

    @StorIOSQLiteColumn(name = "field4", key = true)
    long field4;

    @StorIOSQLiteColumn(name = "field5")
    float field5;

    @StorIOSQLiteColumn(name = "field6")
    double field6;

    @StorIOSQLiteColumn(name = "field7")
    String field7;

    @StorIOSQLiteColumn(name = "field8")
    byte[] field8;
}