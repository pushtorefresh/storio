package com.pushtorefresh.storio3.sqlite.annotations;

import android.support.annotation.NonNull;

@StorIOSQLiteType(table = "table")
public class WithGeneratedTableMultipleKeys {

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

    @StorIOSQLiteColumn(name = "field7", key = true)
    String field7;

    @StorIOSQLiteColumn(name = "field8")
    byte[] field8;

    @StorIOSQLiteColumn(name = "new_field_1", version = 2)
    int newField1;

    @NonNull
    @StorIOSQLiteColumn(name = "new_field_2", version = 3)
    String newField2;
}