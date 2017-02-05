package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class BoxedTypesMethodsConstructorIgnoreNull {

    private Boolean booleanField;

    private Short shortField;

    private Integer intField;

    private Long longField;

    private Float floatField;

    private Double doubleField;

    @StorIOSQLiteCreator
    public BoxedTypesMethodsConstructorIgnoreNull(Boolean booleanField, Short shortField, Integer intField, Long longField, Float floatField,
                                                  Double doubleField) {
        this.booleanField = booleanField;
        this.shortField = shortField;
        this.intField = intField;
        this.longField = longField;
        this.floatField = floatField;
        this.doubleField = doubleField;
    }

    @StorIOSQLiteColumn(name = "booleanField", ignoreNull = true)
    public Boolean isBooleanField() {
        return booleanField;
    }

    @StorIOSQLiteColumn(name = "shortField", ignoreNull = true)
    public Short getShortField() {
        return shortField;
    }

    @StorIOSQLiteColumn(name = "intField", ignoreNull = true)
    public Integer getIntField() {
        return intField;
    }

    @StorIOSQLiteColumn(name = "longField", key = true, ignoreNull = true)
    public Long getLongField() {
        return longField;
    }

    @StorIOSQLiteColumn(name = "floatField", ignoreNull = true)
    public Float getFloatField() {
        return floatField;
    }

    @StorIOSQLiteColumn(name = "doubleField", ignoreNull = true)
    public Double getDoubleField() {
        return doubleField;
    }
}
