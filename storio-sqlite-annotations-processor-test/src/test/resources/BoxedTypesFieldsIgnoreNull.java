package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class BoxedTypesFieldsIgnoreNull {

    @StorIOSQLiteColumn(name = "booleanField", ignoreNull = true)
    Boolean booleanField;

    @StorIOSQLiteColumn(name = "shortField", ignoreNull = true)
    Short shortField;

    @StorIOSQLiteColumn(name = "intField", ignoreNull = true)
    Integer intField;

    @StorIOSQLiteColumn(name = "longField", key = true, ignoreNull = true)
    Long longField;

    @StorIOSQLiteColumn(name = "floatField", ignoreNull = true)
    Float floatField;

    @StorIOSQLiteColumn(name = "doubleField", ignoreNull = true)
    Double doubleField;
}
