package com.pushtorefresh.storio2.sqlite.annotations;

@StorIOSQLiteType(table = "table")
public class PrivateFieldsWithoutCreator {

    @StorIOSQLiteColumn(name = "field1")
    private boolean field1;

    @StorIOSQLiteColumn(name = "field2")
    private short field2;

    @StorIOSQLiteColumn(name = "field3")
    private int field3;

    @StorIOSQLiteColumn(name = "field4", key = true)
    private long field4;

    @StorIOSQLiteColumn(name = "field5")
    private float field5;

    @StorIOSQLiteColumn(name = "field6")
    private double field6;

    @StorIOSQLiteColumn(name = "field7")
    private String field7;

    @StorIOSQLiteColumn(name = "field8")
    private byte[] field8;

    public boolean isField1() {
        return field1;
    }

    public short getField2() {
        return field2;
    }

    public int getField3() {
        return field3;
    }

    public long getField4() {
        return field4;
    }

    public float getField5() {
        return field5;
    }

    public double getField6() {
        return field6;
    }

    public String getField7() {
        return field7;
    }

    public byte[] getField8() {
        return field8;
    }
}