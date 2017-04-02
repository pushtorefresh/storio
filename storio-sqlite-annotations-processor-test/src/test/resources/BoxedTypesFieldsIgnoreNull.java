package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class BoxedTypesFieldsIgnoreNull {

    @StorIOSQLiteColumn(name = "field1", ignoreNull = true)
    Boolean field1;

    @StorIOSQLiteColumn(name = "field2", ignoreNull = true)
    Short field2;

    @StorIOSQLiteColumn(name = "field3", ignoreNull = true)
    Integer field3;

    @StorIOSQLiteColumn(name = "field4", key = true, ignoreNull = true)
    Long field4;

    @StorIOSQLiteColumn(name = "field5", ignoreNull = true)
    Float field5;

    @StorIOSQLiteColumn(name = "field6", ignoreNull = true)
    Double field6;
}