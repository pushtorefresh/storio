package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class BoxedTypesMethodsConstructor {

    private Boolean booleanField;

    private Short shortField;

    private Integer intField;

    private Long longField;

    private Float floatField;

    private Double doubleField;

    @StorIOSQLiteCreator
    public BoxedTypesMethodsConstructor(Boolean booleanField, Short shortField, Integer intField, Long longField, Float floatField,
                                        Double doubleField) {
        this.booleanField = booleanField;
        this.shortField = shortField;
        this.intField = intField;
        this.longField = longField;
        this.floatField = floatField;
        this.doubleField = doubleField;
    }

    @StorIOSQLiteColumn(name = "booleanField")
    public Boolean isBooleanField() {
        return booleanField;
    }

    @StorIOSQLiteColumn(name = "shortField")
    public Short getShortField() {
        return shortField;
    }

    @StorIOSQLiteColumn(name = "intField")
    public Integer getIntField() {
        return intField;
    }

    @StorIOSQLiteColumn(name = "longField", key = true)
    public Long getLongField() {
        return longField;
    }

    @StorIOSQLiteColumn(name = "floatField")
    public Float getFloatField() {
        return floatField;
    }

    @StorIOSQLiteColumn(name = "doubleField")
    public Double getDoubleField() {
        return doubleField;
    }
}
