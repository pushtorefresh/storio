package com.pushtorefresh.storio.sample.db.entities;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

/**
 * Just a sample entity with all supported types of type mapping via annotation processor.
 */
@StorIOSQLiteType(table = "there_is_no_such_table")
public class AllSupportedTypes {

    @StorIOSQLiteColumn(name = "some_boolean")
    boolean someBoolean;

    @StorIOSQLiteColumn(name = "some_boolean_object")
    Boolean someBooleanObject;

    @StorIOSQLiteColumn(name = "some_short")
    short someShort;

    @StorIOSQLiteColumn(name = "some_short_object")
    Short someShortObject;

    @StorIOSQLiteColumn(name = "some_integer", key = true) // One field should be key
    int someInteger;

    @StorIOSQLiteColumn(name = "some_integer_object")
    Integer someIntegerObject;

    @StorIOSQLiteColumn(name = "some_long")
    long someLong;

    @StorIOSQLiteColumn(name = "some_long_object")
    Long someLongObject;

    @StorIOSQLiteColumn(name = "some_float")
    float someFloat;

    @StorIOSQLiteColumn(name = "some_float_object")
    Float someFloatObject;

    @StorIOSQLiteColumn(name = "some_double")
    double someDouble;

    @StorIOSQLiteColumn(name = "some_double_object")
    Double someDoubleObject;

    @StorIOSQLiteColumn(name = "some_string")
    String someString;

    @StorIOSQLiteColumn(name = "some_byte_array")
    byte[] someByteArray;

}
