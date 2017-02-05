package com.pushtorefresh.storio.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrimitiveMethodsConstructor {

    private boolean booleanField;

    private short shortField;

    private int intField;

    private long longField;

    private float floatField;

    private double doubleField;

    private String stringField;

    private byte[] byteArrayField;

    @StorIOSQLiteCreator
    public PrimitiveMethodsConstructor(boolean booleanField, short shortField, int intField, long longField, float floatField,
                                       double doubleField, String stringField, byte[] byteArrayField) {
        this.booleanField = booleanField;
        this.shortField = shortField;
        this.intField = intField;
        this.longField = longField;
        this.floatField = floatField;
        this.doubleField = doubleField;
        this.stringField = stringField;
        this.byteArrayField = byteArrayField;
    }

    @StorIOSQLiteColumn(name = "booleanField")
    public boolean isBooleanField() {
        return booleanField;
    }

    @StorIOSQLiteColumn(name = "shortField")
    public short getShortField() {
        return shortField;
    }

    @StorIOSQLiteColumn(name = "intField")
    public int getIntField() {
        return intField;
    }

    @StorIOSQLiteColumn(name = "longField", key = true)
    public long getLongField() {
        return longField;
    }

    @StorIOSQLiteColumn(name = "floatField")
    public float getFloatField() {
        return floatField;
    }

    @StorIOSQLiteColumn(name = "doubleField")
    public double getDoubleField() {
        return doubleField;
    }

    @StorIOSQLiteColumn(name = "stringField")
    public String getStringField() {
        return stringField;
    }

    @StorIOSQLiteColumn(name = "byteArrayField")
    public byte[] getByteArrayField() {
        return byteArrayField;
    }
}
